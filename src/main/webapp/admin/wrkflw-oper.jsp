<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>Pasos de Gestión</title>
		<link rel="stylesheet" type="text/css" href="../css/gestion.css">
		<link type="text/css" href="../css/scrolltable.css" rel="stylesheet">
		<script type="text/javascript" src="../js/dojo.js"></script>
		<script type="text/javascript" src="../js/wrkflw-utils.js"></script>
		<script type="text/javascript" src="../js/wrkflw-oper.js"></script>
		<style type="text/css">
		html body {
			margin: 0px;
			padding: 0px;
			border: 0px;
			background-color: #f0f0f0;
			overflow: hidden;
		}
		fieldset {
			margin: 1px;
			padding: 1px;
		}
		</style>
	</head>
	
	<body>
		<table width="100%">
			<tr>
				<td>
					<fieldset>
						<legend>&nbsp;Operaciones&nbsp;del&nbsp;Caso&nbsp;</legend>
						<table width="100%">
							<tr>
								<td>
									<div id="tableContainer" class="tableContainer" style="height: 250;">
										<table id="tblOper"  class="scrollTable">
											<thead class="fixedHeader" id="fixedHeader">
												<tr>
													<th>&nbsp;</th>
													<th>L&iacute;nea</th>
													<th>Nombre</th>
													<th>Responsable</th>
													<th>Descripci&oacute;n</th>
													<th>Responsable Siguiente</th>
													<th>Operaci&oacute;n Siguiente</th>
												</tr>
											</thead>
											<tbody class="scrollContent">
												<tr>
													<td colspan="7">Sin Operaciones</td>
												</tr>
											</tbody>
										</table>
									</div>
								</td>
							</tr>
							<tr>
								<td align="right">
									<table>
										<tr>
											<td><input id="pb_o_new" type="button" value="Nuevo" onclick="parent.modoOper(1)" style="display: none;"></td>
											<td><input id="pb_o_edit" type="button" value="Editar" onclick="parent.modoOper(2)" style="display: none;"></td>
											<td><input id="pb_o_del" type="button" value="Borrar" onclick="parent.modoOper(3)" style="display: none;"></td>
											<td><input id="pb_o_show" type="button" value="Ver Detalle" onclick="return parent.modoOper(4)" style="display: none;"></td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</fieldset>
				</td>
			</tr>
		</table>
	</body>
</html>
