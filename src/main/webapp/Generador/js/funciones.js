function desactivaBackspace(event){
	var d = $(event.srcElement || event.target);
	var disabled = d.prop("readonly") || d.prop("disabled");
    if( (window.event.srcElement.tagName.toUpperCase() == 'INPUT' 
    	|| window.event.srcElement.tagName.toUpperCase() == 'TEXTAREA') 
    	&& disabled
    	&& (window.event.keyCode==8 || window.event.keyCode==13) ){
    		return false;
    }
    var nombre=window.event.srcElement.tagName.toUpperCase();
    if( (!(nombre=="INPUT") &&  !(nombre=='TEXTAREA')) && (window.event.keyCode==8 || window.event.keyCode==13 )){					
    	return false;
    }
    if (document.activeElement.getAttribute('type')=='select-one' && (window.event.keyCode==8 || window.event.keyCode==13)) { 
    	return false; 
    }
    if(window.event.keyCode==27){
    	return false;
    }
    return true;
}
function nonWorkingDates(date){
    var day = date.getDay(), Sunday = 0, Monday = 1, Tuesday = 2, Wednesday = 3, Thursday = 4, Friday = 5, Saturday = 6;
    //var closedDates = [[7, 29, 2009], [8, 25, 2010]];
    var closedDays = [[Sunday], [Saturday]];
    for (var i = 0; i < closedDays.length; i++) {
        if (day == closedDays[i][0]) {
            return [false];
        }
    }
    return [true];
}
function clonaSelect(id1,id2){
	$('#'+id1).find('option').clone().appendTo('#'+id2);
}
function readOnlyInput(){
	$('input').prop('readonly', true);
}
function readOnlyTextArea(){
	$('textarea').prop('readonly', true);
}
function cssDisabledInput(){
	$("input").css("background-color", "#E8E8E8");
}
function habilitaInput(id){
	$('#'+id).prop('readonly', false);
	$("#"+id).css("background-color", "#FFFFFF");
}
function cssDisabledTextArea(){
	$("textarea").css("background-color", "#E8E8E8");
}
function disableSelect(){
	$('select').prop('disabled', true);
}
function enabledSelect(){
	$('select').prop('disabled', false);
}
function disableCheckbox(){
	$('input:checkbox').prop('disabled', true);
}
function enabledCheckbox(){
	$('input:checkbox').prop('disabled', false);
}
function cerrarMuestraObservaciones(){
	readOnlyTextArea();
	cssDisabledTextArea();
	$( "#observacionesDiv" ).dialog("close");
}
function muestraObservaciones(){
	$('textarea').prop('readonly', false);
	$("textarea").css("background-color", "#FFFFFF");
    $("#observacionesDiv" ).
          dialog({
                width: 400,
                height:260,
                closeOnEscape: true,
        		modal: true
          });
}

function llenaCombo(data,name){
	var $select = $('#'+name);
	if(data!=null && data.length>0){
		for (var i = 0; i < data.length; i++){
		    var obj = data[i];
			$select.append($('<option />', { value: obj.Id, text: obj.Descripcion }));
		}	
	}	
}

function vaciarJsonAInputs(data){
	$.each(data, function(k,v){
        if(v.HAYINFO){
        	$.each(v, function(i,valor){
            	if(i!="HAYINFO"){
            		$('#'+i).val(valor);
            	}
	        });
        }
    });
}
function onlyIntegers(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	return (keyPressed >= 48 && keyPressed <= 57);
}
function obtieneFechaMasReciente(fecha1,fecha2){
	var fechauno = new Date(fecha1);
	var fechados = new Date(fecha2);
	//var resultado = fechauno.getTime() === fechados.getTime();
	if(fechauno.getTime() > fechados.getTime()){
		return fecha1
	}else{
		return fecha2
	}
}
function validateDecimal(dlt) {
	var re=/^([0-9]{1,5})(\.)?([0-9]{1,2})$/;
	var val=$("#"+dlt.id).val();
	var OK = re.test(val);  
    if (!OK) {
    	$("#"+dlt.id).val(0)
    }
    $("#"+dlt.id).formatCurrency();
}
function unFrmt (dlt){
	var val=$("#"+dlt.id).val();
	val = val.replace("$", "");
   	val = val.replace(/,/g, "");
   	val = val.replace(/\s/, "");
   	if ( val.indexOf( "(" ) >= 0 ) {
		val = val.replace("(", "");
		val = val.replace(")", "");
		val = "-" + val;
   	}
   	$("#"+dlt.id).val(val);
}
function unFrmt2 (val){
	val = val.replace("$", "");
   	val = val.replace(/,/g, "");
   	val = val.replace(/\s/, "");
   	if ( val.indexOf( "(" ) >= 0 ) {
		val = val.replace("(", "");
		val = val.replace(")", "");
		val = "-" + val;
   	}
   	return val;
}
function uppercasse(dlt) {
	var val=$("#"+dlt.id).val();
	var res = val.toUpperCase();
    $("#"+dlt.id).val(res);
}
function quitaCaratEspecial(dlt) {
	var cad=$("#"+dlt.id).val();
	cad=cad.replace(/[^a-zA-Z 0-9]+/g,'');
    $("#"+dlt.id).val(cad);
}
function quitaCaratEspecial2(valor) {
	var cad=valor;
	cad=cad.replace(/[^a-zA-Z 0-9]+/g,'');
    return cad;
}
function cleanSelectsInputsTextArea(idDiv){
	$('#'+idDiv).find('select').empty();
	$('#'+idDiv).find('input:text, textarea').val('');
}
function removeOptiosnSelect(idDiv){
	$('#'+idDiv+' option').remove();
}
function cambiaUA(){
	if($("#cCentroContable").val()=="10"){
		$("#cUadministrativa").val("%");
	}
}
function onlyNumbers(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	var strCheck = '0123456789';
	var key = String.fromCharCode(keyPressed);
	if (strCheck.indexOf(key) == -1)
		return false; 
	return true;
}
function onlyDoubles(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	var strCheck = '0123456789.';
	var key = String.fromCharCode(keyPressed);
	if (strCheck.indexOf(key) == -1)
		return false; 
	return true;
}
function ChangeCase(elem) {//Cambia a Mayusculas
	elem.value = elem.value.toUpperCase();
}
function notWritte(){
	return false; 
}
function no_backspaces(event){
    backspace = 8;
    if (event.keyCode == backspace) event.preventDefault();
}
function ArrayTabla(id){
	var arregloTmp=new Array();
	var arrayFila=new Object();
	
	var aTrs = $('#'+id).dataTable().fnGetNodes();
	var nTr;
	var jqInputs;
	for ( var i=0 ; i<aTrs.length; i++ ){
		nTr =  $('#'+id).dataTable().fnGetData(aTrs[i]);
		//alert("nTr[0]"+nTr[0]);
		arrayFila=[nTr[0],nTr[1],nTr[2],nTr[3],"|"];
		arregloTmp.push(arrayFila);
	}
	return arregloTmp;
}
function arrayTablaPrecom(idTable){
	var arregloTmp=new Array();
	var arrayFila=new Object();
	
	var aTrs = $('#'+idTable).dataTable().fnGetNodes();
	var vimporteP;
	var nTr;
	var jqInputs;
	for ( var i=0 ; i<aTrs.length; i++ )     
	{
		nTr =  $('#'+idTable).dataTable().fnGetData(aTrs[i]);
		jqInputs = $('input',aTrs[i] );
		for ( j=0 ; j < jqInputs.length ; j++ ) {
			var k=j;
			vimporteP = jqInputs[j].value ;
			vimporteP = quitaFmt(vimporteP);
			arrayFila=[nTr[0]+'.'+nTr[1],k+1,vimporteP,"|"];
			arregloTmp.push(arrayFila);
		}
	}
	
	return arregloTmp;
}
function fnGetSelected( oTableLocal ){
	var aReturn = new Array();
	var aTrs = oTableLocal.fnGetNodes();
	for ( var i=0 ; i<aTrs.length ; i++ ){
		if ( $(aTrs[i]).hasClass('row_selected') ){
			aReturn.push( aTrs[i] );
		}
	}
	return aReturn;
}
function quitaFmt( val ) {
   	val = val.replace("$", "");
   	val = val.replace(",", "");
   	val = val.replace(",", "");
   	val = val.replace(",", "");
   	val = val.replace(",", "");
   	if ( val.indexOf( "(" ) >= 0 ) {
		val = val.replace("(", "");
		val = val.replace(")", "");
		val = "-" + val;
   	}
   	return val
}	
function unFormatCurrency2(fld){
	var str=$("#"+fld.id).val();
	str = str.replace("$","");
	str = str.replace(/\,/g,'');
	$("#"+fld.id).val(str);
}
function unFormatCurrency(str){
	str = str.replace("$","");
	str = str.replace(/\,/g,'');
	return parseFloat(str);
}
function fnGetRowDataTable( oTableLocal,idKey ){
	var aTrs = oTableLocal.fnGetNodes();
	var nTr=null;
	for ( var i=0 ; i<aTrs.length ; i++ ){
		nTr =  oTableLocal.fnGetData(aTrs[i]);
		if ( nTr[0]==idKey ){
			return nTr ;
		}
	}
}
function onlyMoney(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	if (keyPressed == 46 || keyPressed == 36)
		return true;
	return (keyPressed >= 48 && keyPressed <= 57);
}
function moneyFormat(evt) {
	$("#"+evt.id).formatCurrency();
}