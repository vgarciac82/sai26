<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
	<title>Administraci&oacute;n de Cuentas Contables</title>

	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/sweetalert2.min.css"></link>
	<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css"/>
	<link rel="stylesheet" type="text/css"href="../css/BloqueoCuentas.css"/>
					
	<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>		
	<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
	
	<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>		
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="../Generador/js/Moment.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>		
	<script type="text/javascript" src="../js/CatalogoCuentas.js"></script>
	
	<script type="text/javascript">
		$(document).ready(function() {
			init();
		});
	</script>
	
	</head>
	<br/>
	<body id="dt_example">
		<form>
			<input type="hidden" name="nNivelBalanza" id="nNivelBalanza">
			<input type="hidden" name="nOrdenBalanza" id="nOrdenBalanza">
			<input type="hidden" name="cBloqueaAbonos" id="cBloqueaAbonos" />
			<input type="hidden" name="cBloqueaCargos" id="cBloqueaCargos" />
			<input type="hidden" name="cNivelBloqueo" id="cNivelBloqueo" />
			<input type="hidden" name="nCuenta" id="nCuenta" />
			
			<div id="container" class="container" style="width: 90%">
				<div class="card-header"> <h3> Mantenimiento de Cuentas Contables </h3> </div>
				<hr class="mt-3"/>
				
				<div id="busquedaDiv">
				</div>
				
				<div class="row">					
					<div class="col-12 col-lg-4 col-md-4 col-sm-12">
						<label for="niveles" class="form-label">N&uacute;mero de Cuenta:</label>
						<div class="row">					
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<input type="text" id="Nivel1" name="Nivel1" class="form-control form-control-sm" style="width: 5em;" size="5" maxlength="5"/>  
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<input type="text" id="Nivel2" name="Nivel2" class="form-control form-control-sm" style="width: 5em;" size="5" maxlength="5"/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<input type="text" id="Nivel3" name="Nivel3" class="form-control form-control-sm" style="width: 5em;" size="5" maxlength="5"/> 
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<input type="text" id="Nivel4" name="Nivel4" class="form-control form-control-sm" style="width: 5em;" size="5" maxlength="5"/> 
							</div>
						</div>
					</div>									
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12">
						<label for="dCuenta" class="form-label">Descripci&oacute;n:</label>						
						<input type="text" id="dCuenta" name="dCuenta" class="form-control form-control-sm"/> 							
					</div>					
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
						<label for="AplicacionCuenta" class="form-label">Aplicaci&oacute;n:</label>
						<select  name="AplicacionCuenta" id="AplicacionCuenta" class="form-select form-select-sm">
							<option value="S"> S&iacute; </option>
							<option value="N"> No </option>
						</select>
					</div>					
				</div>	
				
				<div class="row">					
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<label for="cSubcuenta" class="form-label">Auxiliar:</label>						
						<select  name="cSubcuenta" id="cSubcuenta" class="form-select form-select-sm">
							<option value=""></option>
							<option value="ALM">  Almacen </option>
							<option value="CTAB"> Cuenta Banc&aacute;ria </option>
							<option value="RFC">  RFC </option>
							<option value="EP">   EP </option>
						</select>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<label for="TipoCuenta" class="form-label">Tipo:</label>						
						<select  name="TipoCuenta" id="TipoCuenta" class="form-select form-select-sm">
							<option id="P" value="P"> Presupuesto </option>
							<option id="B" value="B"> Balance </option>
						</select>  						
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<label for="TipoBalance" class="form-label">Balance:</label>						
						<select  name="TipoBalance" id="TipoBalance" class="form-select form-select-sm">
							<option value="O"> Orden </option>
							<option value="B"> Balance </option>
							<option value="P"> Presupuesto </option>
							<option value="R"> Resultado </option>
						</select>						
					</div>					
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						<label for="NaturalezaCuenta" class="form-label">Naturaleza:</label>
						<select  name="NaturalezaCuenta" id="NaturalezaCuenta" class="form-select form-select-sm">
							<option value="A"> Acreedora </option>
							<option value="D"> Deudora </option>
						</select>
					</div>					
				</div>	
				
				<div class="row">					
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<label for="NivelCuenta" class="form-label">Nivel:</label>						
						<input type="text" id="NivelCuenta" name="NivelCuenta" class="form-control form-control-sm" style="width: 5em;" maxlength="2" readonly/> 		
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						<label for="nCuentaLike" class="form-label">Cuenta Like:</label>						
						<input type="text" id="nCuentaLike" name="nCuentaLike" class="form-control form-control-sm" style="width: 10em;" maxlength="30" readonly/> 		
					</div>
				</div>													
				
				<div class="row">					
					<div class="col-12 col-lg-4 col-md-4 col-sm-12">
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12">																						
						<input type="button" id="Agregar" name="Agregar" class="btn btn-secondary btn-sm" value="Agregar"/>																	 																																										
						<input type="button" id="Buscar" name="Buscar" class="btn btn-secondary btn-sm" value="Buscar"/>																	 																																											
						<input type="button" id="Limpiar" name="Limpiar" class="btn btn-secondary btn-sm" value="Limpiar"/>																	 																																											
						<input type="button" id="Actualizar" name="Actualizar" class="btn btn-secondary btn-sm" value="Actualizar" style="display: none"/>																	 																																											
						<input type="button" id="Eliminar" name="Eliminar" class="btn btn-secondary btn-sm" value="Eliminar" style="display: none"/>																	 																	
					</div>	
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">																						
					</div>	
				</div>
				
				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<div class="table-responsive">	    
							<table id="dTbl" class="table table-striped table-bordered" >
								<thead>
									<tr>
										<th>Cuenta</th>
										<th>Descripci&oacute;n</th>
										<th>Tipo de Cuenta</th>
										<th>Cuenta Padre</th>
										<th>Tipo Balance</th>
										<th>Naturaleza</th>
										<th>Nivel</th>
										<th>Aplicacion</th>
										<th>Auxiliar</th>
										<th>Cuenta Like</th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
				
				
			</div>
		</form>
		<div id="question" style="display: none; cursor: default">
			<h1 style="font-size: 13pt; color: graytext; font-weight: bold;">
				<span id="titulo">Informaci&oacute;n</span>
			</h1>
			<div id="resultMsg">
			</div>
		</div>
	</body>
</html>
