function inicioOnSubmit() {
	window.parent.gestion.setRespCaptura(window.parent.gestion.getUserName());
	window.parent.gestion.setEjecResp(window.parent.gestion.getUserName());

	window.parent.gestion.setNombre(document.getElementById("nombre").value);
	window.parent.gestion.setAppaterno(document.getElementById("apPaterno").value);
	window.parent.gestion.setApmaterno(document.getElementById("apMaterno").value);
	window.parent.gestion.setRfc(document.getElementById("rfc").value);
	window.parent.gestion.setCentronegocio(window.parent.gestion.getUserProp("CENTRONEGOCIO"));
	var today = new Date();
	window.parent.gestion.setFecha(today.getTime());
	window.parent.gestion.setEstatusSol(document.getElementById("EstatusSol").value);

	var elem;
	var errs=0;

	if (isWhitespace(document.getElementById("nombre").value)) errs += 1; 
	if (isWhitespace(document.getElementById("apPaterno").value)) errs += 1; 
	if (isWhitespace(document.getElementById("apMaterno").value)) errs += 1; 
	if (isWhitespace(document.getElementById("rfc").value)) errs += 1; 
	if (isWhitespace(document.getElementById("curp").value)) errs += 1; 
	if (isWhitespace(document.getElementById("numIntFam").value)) errs += 1; 
	if (isWhitespace(document.getElementById("numIntFamEcoAct").value)) errs += 1; 
	if (isWhitespace(document.getElementById("numDependtesEco").value)) errs += 1; 
/*
	if (isWhitespace(document.getElementById("domicilioActual").value)) errs += 1; 
	if (isWhitespace(document.getElementById("coloniaActual").value)) errs += 1; 
	if (isWhitespace(document.getElementById("cpActual").value)) errs += 1; 
	if (isWhitespace(document.getElementById("municipioActual").value)) errs += 1; 
	if (isWhitespace(document.getElementById("estadoActual").value)) errs += 1; 
	if (isWhitespace(document.getElementById("aniosRecidenciaActual").value)) errs += 1; 
	if (isWhitespace(document.getElementById("telPartActual").value)) errs += 1; 
	if (isWhitespace(document.getElementById("situVivienda").value)) errs += 1; 
	if (isWhitespace(document.getElementById("nomPropietario").value)) errs += 1; 

	if (isWhitespace(document.getElementById("domicilioAnterior").value)) errs += 1; 
	if (isWhitespace(document.getElementById("coloniaAnterior").value)) errs += 1; 
	if (isWhitespace(document.getElementById("cpAnterior").value)) errs += 1; 
	if (isWhitespace(document.getElementById("municipioAnterior").value)) errs += 1; 
	if (isWhitespace(document.getElementById("estadoAnterior").value)) errs += 1; 
	if (isWhitespace(document.getElementById("aniosRecidenciaAnterior").value)) errs += 1; 

	if (isWhitespace(document.getElementById("nombreConyuge").value)) errs += 1; 
	if (isWhitespace(document.getElementById("apPaternoConyuge").value)) errs += 1; 
	if (isWhitespace(document.getElementById("apMaternoConyuge").value)) errs += 1; 
	if (isWhitespace(document.getElementById("rfcConyuge").value)) errs += 1; 
	if (isWhitespace(document.getElementById("curpConyuge").value)) errs += 1; 
*/
	if (errs>1)  alert('Hay campos que no han sido capturados y son requeridos');
	if (errs==1) alert('Hay campos que no han sido capturados y son requeridos');

	return (errs!=0);

}

function getRbValue(rb) {
	 for (var i=0; i<rb.length; i++) 
	 	if (rb[i].checked) {
			//alert('Entro al checked =' + rb[i].value);
			return rb[i].value;
		}
	 return -1;
	
}