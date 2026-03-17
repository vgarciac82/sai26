  
	
		<form name="envioSICOP" id="envioSICOP" action="../gstnmngr/generaLayoutCompromisos" method="post">
		<div id="containerEnvio" style="width: 90%" >
			<div>	
				<input type="hidden" id="sDataHCompromiso" name="sDataHCompromiso" />
				<input type="hidden" id="sDataHContrato" name="sDataHContrato" />
			</div>
				<div id="tblEnvio" class="table-responsive">
					<table id="dt_paraEnvio" class="table table-striped table-bordered">
						<thead>
							<tr align="center">
								<th scope="col">C.C.</th>
								<th scope="col">Compromisos</th>
								<th scope="col">Documento</th>
								<th scope="col">Tipo Documento</th>
								<th scope="col">Estado</th>
								<th scope="col">Fecha de carga</th>
								<th scope="col">Fecha de aplicaci&oacute;n</th>
								<th scope="col">Ramo</th>
								<th scope="col">Unidad</th>
								<th scope="col">RFC</th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
				<div class="row pt-3 col-1 m-1">
					<input type="button" onClick="generar();" class="btn btn-secondary" value ="Generar Layout" id="genera"/>
				</div>
			
		</div>
	</form>
	