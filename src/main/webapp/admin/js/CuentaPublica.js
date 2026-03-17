/**
 * Inicializa los objetos de la pagina.
 */
function init() {
	$("#accordion").accordion();
}

/**
 * Funcion que abre una ventana para la consulta de reportes
 * 
 * @param reportName
 *            Nombre del reporte.
 */
function openReportWindow(reportName, isSP) {

	var url;
/*
	if (reportName == 'C12AD165')
		url = "../reports/CuentaPublica?isSP="
				+ isSP
				+ "&reportType="
				+ reportName
				+ "&condicion="
				+ encodeURI(" AND aEjercicioFiscal_1 = ''''2012'''' AND cPartida_10 like ''''43%''''")
				+ "&condicion=" + encodeURI("''''81101'''', ''''82105'''', ''''82108''''")
				+ "&condicion=" + encodeURI("cSubCuenta") 
				+ "&condicion=" + encodeURI("cSubCuenta") 
				+ "&condicion=" + encodeURI("mSaldo12") 
				+ "&condicion=" + encodeURI("GENERAL")
				+ "&condicion=" + encodeURI("EMULGADO");
	else
*/
		url = "../reports/CuentaPublica?isSP=" + isSP + "&reportType="
				+ reportName;
	var ventimp = window.open(url, "popacuse",
			"scrollbars=1, resizable=yes, width=512, height=300");

}