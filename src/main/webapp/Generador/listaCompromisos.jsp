
		<div id="listaCompromisos" class="mt-3"  style="width: 90%">
	  		<form name="conLayout" id="conLayout" action="../gstnmngr/CancelaCompromisos" method="post">
		    	<input type="hidden" id="comp" name="comp" value=""/>
				<div id="demo_jui2">
		        	<input type = "button" id="pbStatusInicial" name="pbStatusInicial" onClick="Inicializa();" value="Regresa Layout" class="btn btn-secondary"/>
		        	<div class="row col-2 pt-3">
						<div class="form-check mx-3">
						  <input class="form-check-input" type="checkbox" id="chkSelTodos" name="chkSelTodos" value="" >
						  <label class="form-check-label" for="flexCheckDefault">
						    Seleccionar Todos
						  </label>
						</div>
					</div>
		        	<div id="divTabla1" class="table-responsive">		        		        	
						<table id="dt_enviados" class="table table-striped table-bordered">						
							<thead>
								<tr align="center">
									<th scope="col">Sel.</th>
									<th scope="col">C.C.</th>
									<th scope="col">Compromiso</th>
									<th scope="col">Documento</th>
									<th scope="col">Tipo Documento</th>
									<th scope="col">Estado</th>
									<th scope="col">Fecha</th>
									<th scope="col">Ramo</th>
									<th scope="col">Unidad</th>
									<th scope="col">RFC</th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</div>
				</div>
			</form>
		</div>
	