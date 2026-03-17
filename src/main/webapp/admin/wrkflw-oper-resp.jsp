<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>Siguiente Responsable Operaci&oacute;n</title>
		<link rel="stylesheet" type="text/css" href="../css/gestion.css">
		<link type="text/css" href="../css/scrolltable.css" rel="stylesheet">
		<script type="text/javascript" src="../js/wrkflw-utils.js"></script>
		<script type="text/javascript" src="../js/wrkflw-oper-resp.js"></script>
		<style type="text/css">
		html body {
			height: 100%;
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
		<fieldset>
			<legend>&nbsp;Siguiente&nbsp;</legend>
			<table width="100%" cellpadding="0" cellspacing="0">
				<tr>
					<td>
						<div id="tableContainer" class="tableContainer" style="height: 100;">
							<table id="tblRespOper"  class="scrollTable">
								<thead class="fixedHeader" id="fixedHeader">
									<tr>
										<th>&nbsp;</th>
										<th>Responsable</th>
										<th>Operaci&oacute;n</th>
									</tr>
								</thead>
								<tbody class="scrollContent">
									<tr>
										<td colspan="3">Sin Operaci&oacute;n/Responsable</td>
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
								<td><input id="pb_ro_new" type="button" value="Nuevo" onclick="return parent.modoRespOper(1)" style="display: none;"></td>
								<td><input id="pb_ro_edit" type="button" value="Editar" onclick="return parent.modoRespOper(2)" style="display: none;"></td>
								<td><input id="pb_ro_del" type="button" value="Borrar" onclick="return parent.modoRespOper(3)" style="display: none;"></td>
								<td><input id="pb_ro_show" type="button" value="Ver Detalle" onclick="return parent.modoRespOper(4)" style="display: block;"></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</fieldset>
	</body>
</html>
