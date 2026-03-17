<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<form>
<input type="hidden" id="cTipoPago" name="cTipoPago" value="">
<input type="hidden" id="nFolioPago" name="nFolioPago" value="">
<input type="hidden" id="nenviadosicop" name="nenviadosicop" value="">
<input type="hidden" id="cEstatus" name="cEstatus" value="">

</form>
<div class="container my-5">
    <h1 class="mb-4">Compromiso</h1>

    <div class="card m-3">
    <div class="card-header">
        <h5 class="mb-0">Contrato</h5>
    </div>
    <div class="card-body">
        <div class="row g-3">
            <div class="col-md-4">
                <label for="claveContrato" class="form-label">Clave de Contrato</label>
                <input type="text" class="form-control" id="claveContrato" value="">
            </div>
            <div class="col-md-4">
                <label for="tipoContrato" class="form-label">Tipo Contrato</label>
                <input type="text" class="form-control" id="tipoContrato">
            </div>
            <div class="col-md-4">
                <label for="tipoDocumento" class="form-label">Tipo de Documento</label>
                <input type="text" class="form-control" id="tipoDocumento">
            </div>

            <div class="col-md-6">
                <label for="unidadAdministrativa" class="form-label">Unidad Administrativa</label>
                <input type="text" class="form-control" id="unidadAdministrativa">
            </div>
            <div class="col-md-6">
                <label for="tipoAdjudicacion" class="form-label">Tipo de Adjudicación</label>
                <input type="text" class="form-control" id="tipoAdjudicacion">
            </div>
            
            <div class="col-md-4">
                <label for="ruc" class="form-label">R.F.C.</label>
                <input type="text" class="form-control" id="ruc">
            </div>
            <div class="col-md-8">
                <label for="nombre" class="form-label">Nombre / Razón Social</label>
                <input type="text" class="form-control" id="nombre">
            </div>

            <div class="col-12">
                <label for="conceptoContrato" class="form-label">Concepto Contrato</label>
                <textarea class="form-control" id="conceptoContrato" rows="6"></textarea>
            </div>
        </div>
    </div>
</div>

    <div class="card  m-3">
        <div class="card-header">
            <h5 class="mb-0">Resumen de Montos</h5>
        </div>
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-4">
                    <label for="montoContrato" class="form-label"><b>Monto Contrato</b></label>
                    <input type="text" class="form-control" id="montoContrato" value="" readonly>
                </div>
                <div class="col-md-4">
                    <label for="compromisoActual" class="form-label"><b>Compromiso Actual</b></label>
                    <input type="text" class="form-control" id="compromisoActual" value="" readonly>
                </div>
                
            </div>
        </div>
    </div>

    <div class="card m-3">
        <div class="card-header ">
            <h5 class="mb-0">Presupuesto Comprometido</h5>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table id="tablaPresupuestoComprometido" class="table table-striped table-bordered w-100">
                    <thead>
                        <tr>
                            <th>EP</th>
                            <th>Ene</th>
                            <th>Feb</th>
                            <th>Mar</th>
                            <th>Abr</th>
                            <th>May</th>
                            <th>Jun</th>
                            <th>Jul</th>
                            <th>Ago</th>
                            <th>Sep</th>
                            <th>Oct</th>
                            <th>Nov</th>
                            <th>Dic</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
     <!-- Documentos del pago -->
     <div class="card m-3">
         <div class="card-header">
             <h5 class="mb-0">Documentos del Tramite</h5>
         </div>
         <div class="card-body">
             <div class="table-responsive">
                 <table id="tblFiles" class="table table-striped table-bordered w-100">
                     <thead>
                         <tr class="text-center">
                             <th>Documento</th>
                             <th>Consultar</th>
                         </tr>
                     </thead>
                     <tbody id="bodyDoctos">
                     </tbody>
                 </table>
             </div>
         </div>
     </div>
    
</div>


<script src="js/ResumenCompromiso.js"> </script>
 
