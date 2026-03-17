<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>Reportes de Cuenta P&uacute;blica</title>
		<link href="js/jq9/css/smoothness/jquery-ui-1.9.0.custom.css"
			rel="stylesheet">
		<link href="../Generador/css/demo_page.css" rel="stylesheet">
		<link href="../Generador/css/demo_table_jui.css" rel="stylesheet">
		<script type="text/javascript" src="js/jq9/jquery-1.8.2.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="js/jq9/jquery-ui-1.9.0.custom.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript"
			src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript"
			src="../js/ContabilidadCentroContable.js"></script>
		<script type="text/javascript" src="js/CuentaPublica.js"></script>
		<script type="text/javascript">
	var dTable;
	$(document).ready(function() {
		init();
	});
</script>
	</head>

	<body id="dt_example">
		<h1>
			<label id="titulo">
				Reportes de Cuenta P&uacute;blica
			</label>
		</h1>
		<div id="container" class="container SyCData">
			<div id="accordion">
				<h3>
					Financiera
				</h3>
				<div>
					<ul>
						<li>
							<a href="#"
								onclick="javascript:openReportWindow('C11IF045', true)"> <b>C11IF045
							</b>HOJA DE TRABAJO </a>
						</li>
						<li>
							<a href="#"
								onclick="javascript:openReportWindow('C11IF050', true)"> <b>C11IF050
							</b>VARIACIONES DE LAS CUENTAS DE BALANCE </a>
						</li>
						<!-- 
						<li>
							<a href=""> <b>C11IF055 </b>COMENTARIOS A LAS PRINCIPALES
								VARIACIONES DE LAS CUENTAS DE BALANCE Y RESULTADOS </a>
						</li>
						 
						<li>
							<a href=""> <b>C11IF065 </b>RECUPERACIONES REGISTRADAS </a>
						</li>
						-->
						<li>
							<a href="#"
								onclick="javascript:openReportWindow('C11IF070', true)"> <b>C11IF070
							</b>CUENTAS DE ENLACE</a>
						</li>
						<li>
							<a href="#"
								onclick="javascript:openReportWindow('C11IF075', true)"> <b>C11IF075
							</b>RECTIFICACIONES AL EJERCICIO DEL PRESUPUESTO DE AÑOS ANTERIORES </a>
						</li>
						<li>
							<a href="#"
								onclick="javascript:openReportWindow('C11IF080', true)"> <b>C11IF080
							</b>AFECTACIONES A CUENTAS PATRIMONIALES Y DE INVENTARIOS Y BIENES
								MUEBLES </a>
						</li>
						<li>
							<a href="#"
								onclick="javascript:openReportWindow('C11IF085', true)"> <b>C11IF085
							</b>AN&Aacute;LISIS DE LA INCIDENCIA CONTABLE DEL PRESUPUESTO </a>
						</li>
						<li>
							<a href="#"
								onclick="javascript:openReportWindow('C11IF090', true)"> <b>C11IF090
							</b>INTEGRACIÓN DEL COSTO DE OPERACI&Oacute;N DE PROGRAMAS </a>
						</li>
					</ul>
				</div>
				<h3>
					Presupuestal
				</h3>
				<div>
					<ul>
						<li>
							<a href="#"
								onclick="javascript:openReportWindow('C12IF190', true)"> <b>C12IF190
							</b>ESTADO DEL EJERCICIO DEL PRESUPUESTO </a>
						</li>
						<!-- 
						<li>
							<a href=""> <b>C12IF195 </b>COMPORTAMIENTO DEL GASTO
								DEVENGADO NO PAGADO DEL EJERCICIO ANTERIOR </a>
						</li>
						 -->
						<li>
							<a href="#"
								onclick="javascript:openReportWindow('C12AD155', true)"> <b>C12AD155
							</b>ANÁLISIS DEL GASTO POR FUNCIÓN Y UNIDAD RESPONSABLE </a>
						</li>
						<li>
							<a href="#"
								onclick="javascript:openReportWindow('C12AD165', true)"> <b>C12AD165
							</b>ANÁLISIS DEL GASTO FEDERALIZADO </a>
						</li>
						<!--
						<li>
							<a href=""> <b>C12AD177 </b>PROGRAMAS Y PROYECTOS FINANCIADOS
								CON CRÉDITOS DE LOS OFIS </a>
						</li>
						-->
						<li>
							<a href="#"
								onclick="javascript:openReportWindow('C22AD360', true)"> <b>C22AD360
							</b>GASTO POR ENTIDAD FEDERATIVA </a>
						</li>
						<li>
							<a href="#"
								onclick="javascript:openReportWindow('C22AD365', true)"> <b>C22AD365
							</b>RECURSOS FEDERALES ENTREGADOS A LAS ENTIDADES FEDERATIVAS A
								TRAVÉS DE CONVENIOS </a>
						</li>
						<!-- 
						<li>
							<a href=""> <b>C22AD370 </b>REMUNERACIONES </a>
						</li>
						<li>
							<a href=""> <b>C22AD375 </b>PLAZAS OCUPADAS POR LAS
								DEPENDENCIAS Y SUS ENTIDADES COORDINADAS </a>
						</li>
						-->
						<li>
							<a href="#"
								onclick="javascript:openReportWindow('C32AP390', true)"> <b>C32AP390
							</b>INVERSIÓN FÍSICA EJERCIDA EN 2012 DEL GOBIERNO FEDERAL POR
								ESTRUCTURA FINANCIERA, ENTIDADES FEDERATIVAS Y FUNCIONES </a>
						</li>
						<li>
							<a href="#"
								onclick="javascript:openReportWindow('C32AP400', true)"> <b>C32AP400
							</b>INVERSIÓN FÍSICA EJERCIDA EN 2012 POR LOS ÓRGANOS DESCONCENTRADOS
								DEL GOBIERNO FEDERAL POR ESTRUCTURA FINANCIERA, ENTIDADES
								FEDERATIVAS Y FUNCIONES </a>
						</li>
						<li>
							<a href="#"
								onclick="javascript:openReportWindow('C32AP405', true)"> <b>C32AP405
							</b>INVERSIÓN FÍSICA EJERCIDA EN 2012 POR CONCEPTO DE APORTACIONES
								FEDERALES, CONVENIOS DE COORDINACIÓN EN MATERIA DE
								DESCENTRALIZACIÓN Y REASIGNACIÓN DE RECURSOS Y SUBSIDIOS A LAS
								ENTIDADES FEDERATIVAS Y LOS MUNICIPIOS POR ESTRUCTURA FINANCIERA
								Y FUNCIONES </a>
						</li>
					</ul>
				</div>
				<h3>
					Servicios Personales
				</h3>
				<div>
				</div>
			</div>
		</div>
	</body>
</html>