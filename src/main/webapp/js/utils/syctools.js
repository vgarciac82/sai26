///////////////////////////////////////////////////////////////////////////////
// VARIABLES GLOBALES
///////////////////////////////////////////////////////////////////////////////
//Detect IE
var isIE = (navigator.appName.indexOf("Explorer") > -1);
var g_jsquery_error;

///////////////////////////////////////////////////////////////////////////////
// MANEJO DE STRING
///////////////////////////////////////////////////////////////////////////////

String.prototype.trim = function() {
	a = this.replace(/^\s+/, '');
	return a.replace(/\s+$/, '');
};

String.prototype.lpad = function(str, n) {
	var pd  = new String(''); 
	var len = this.length;
	var i;
	if (n > len)
	{ 
		for (i = 0; i < (n-len); i++) 
		{
			pd += str;
		} 
	}
	return pd.concat(this); 
}; 

// Removes leading whitespaces
function LTrim( value ) {
	var re = /\s*((\S+\s*)*)/;
	return value.replace(re, "$1");
}

// Removes ending whitespaces
function RTrim( value ) {
	var re = /((\s*\S+)*)\s*/;
	return value.replace(re, "$1");
}

// Removes leading and ending whitespaces
function trim( value ) {
	return LTrim(RTrim(value));
}

///////////////////////////////////////////////////////////////////////////////
// MANEJO DE FECHAS
///////////////////////////////////////////////////////////////////////////////

function getDateAsString(theSecond, theMinute, theHour, theDay, forward) {
	//Variables para calcular la suma de tiempos
	var oneSecond = 1000; //Milliseconds
	var oneMinute = oneSecond * 60;
	var oneHour = oneMinute * 60;
	var oneDay = oneHour * 24;
	
	//var cuantos = new Number(arguments.length);
	
	//alert("arguments.length=["+cuantos+"]");
	//var myTypeOfs = new Array(cuantos);
	//for (i=0; i<cuantos; i++) {
		//myTypeOfs[i]=typeof arguments[i];
		//alert("arguments["+i+"]=["+arguments[i]+"], typeof=["+typeof arguments[i]+"]");
	//}

	var positive = true
	if (typeof arguments[4] == "boolean") {
		positive = forward;
	}
	//alert("positive=["+positive+"]");
	
	var segundos = 0;
	if (typeof arguments[0] == "number") {
		segundos = theSecond * oneSecond;
	}
	//alert("segundos=["+segundos+"]");
	
	var minutos = 0;
	if (typeof arguments[1] == "number") {
		minutos = theMinute * oneMinute;
	}
	//alert("minutos=["+minutos+"]");

	var horas = 0;
	if (typeof arguments[2] == "number") {
		horas = theHour * oneHour;
	}
	//alert("horas=["+horas+"]");

	//alert("arguments.length=["+cuantos+"] Dias theDay=["+theDay+"] typeof theDay =["+typeof theDay+"] typeof arguments[3] =["+typeof arguments[3]+"] myTypeOfs[3]=["+myTypeOfs[3]+"]");
	var dias = 0; //new Number(0);
	if (typeof arguments[3] == "number") {
		//alert("Dias 2");
		//alert("oneDay=["+oneDay+"]");
		dias = new Number(theDay) * oneDay;
	}
	//alert("dias=["+dias+"]");
	
	var addedMillis = dias + horas + minutos + segundos
	addedMillis = addedMillis * ((positive)? 1: -1)
	//alert("addedMillis=["+addedMillis+"]");
	
	var myDate = new Date();
	
	var newDate = myDate.getTime() + addedMillis;
	myDate = new Date(newDate);
	//alert("new myDate=["+myDate+"]");
	var dia = myDate.getDate();
	if (dia.length==1) {
		dia = "0" + dia;
	}
	 
	//alert("5");
	var mes = myDate.getMonth();  
	//alert("6");
	var ayo = 1900 + myDate.getYear(); 
	//alert("7");
	//alert("fecha=["+dia + "/" + mes + "/" + ayo+"]");
	return dia + "/" + mes + "/" + ayo;
}

///////////////////////////////////////////////////////////////////////////////
// MANEJO DE TEXTAREA
///////////////////////////////////////////////////////////////////////////////
// Textarea
var SPECIAL_CHARS  = new Array("\t",     "\n",     "\r");
var TAGS_FOR_CHARS = new Array("&nbsp;", "<br />", "");
var NTEXT_TAGS     = 2; // Numero de TAGS a revertir el remplazo al cargar los datos

function replaceAll(text, strA, strB)  {
	while ( text.indexOf(strA) != -1)
	{
		text = text.replace(strA, strB);
	}
	return text;
}

function replaceLstChar(strObj) {
	var texto = strObj.value;
	for(var i=0; i<SPECIAL_CHARS.length; i++)
	{
		texto = replaceAll(texto, SPECIAL_CHARS[i], TAGS_FOR_CHARS[i]);
	}
	//return valor;
	strObj.value = texto;
}

function restoreLstChar(strObj) {
	var texto = strObj.value;
	for(var i=0; i < NTEXT_TAGS; i++)
	{
		texto = replaceAll(texto, TAGS_FOR_CHARS[i], SPECIAL_CHARS[i]);
	}
	//return valor;
	strObj.value = texto;
}

///////////////////////////////////////////////////////////////////////////////
// JSQUERY-FETCH
///////////////////////////////////////////////////////////////////////////////
// ResultSet (JSQuery)
var gRst   =null;
var gCmd   =0;
var gResult="";

function execQuery(rType, sql, fetchValue, fetchRow) {
	//alert("execQuery(rType, sql)=["+sql+"]");
	var query = new JSQueryFetch(onServerResponse, onServerResponseError);
	query.fetchval = fetchValue;
	query.fetchrow = fetchRow;
	query.execute("stmnt=" + sql);
}

///////////////////////////////////////////////////////////////////////////////

function execServlet(rType, servlet, jdata, opr, fetchValue, fetchRow) {
	var jsquery = new JSQueryFetch(onServerResponse, onServerResponseError);
	jsquery.fetchval = fetchValue;
	jsquery.fetchrow = fetchRow;
	jsquery.url      = jsquery.getURL(servlet);
	//alert("jdata="+jdata);

	clrServerResponseError ();
	jsquery.execute("jdata=" + jdata + "&opr=" + opr);
}

///////////////////////////////////////////////////////////////////////////////

function onServerResponse(resultSet, fetchValue, fetchRow) {
	//alert("onServerResponse");
    setRst(resultSet);
	getResult(fetchValue, fetchRow);
}

///////////////////////////////////////////////////////////////////////////////

function clrServerResponseError () {
	g_jsquery_error = false;
}

function setServerResponseError () {
	g_jsquery_error = true;
}

function getServerResponseError () {
	return g_jsquery_error;
}

///////////////////////////////////////////////////////////////////////////////

function onServerResponseError(status, message) {
	//alert("onServerResponseError: antes alert(), st,msg="+status+","+message);
	setServerResponseError ();
	window.alert(message);
}

///////////////////////////////////////////////////////////////////////////////

function setRst(rst){
	gRst = rst;
}

///////////////////////////////////////////////////////////////////////////////

function getEncabezados(){
	return gRst.column;
}

///////////////////////////////////////////////////////////////////////////////

function getRows(){
	var rows = gRst.row;
	return rows;
}

///////////////////////////////////////////////////////////////////////////////

function getResult(fetchValue, fetchRow) {
	var strHTML = '';
	var rows = getRows().length;

	//alert ("getResult");
	if (rows == 0)
	{
		alert('No se encontraron registros.');
		return;
	}

	// Procesa cada registro    
   	for (var i = 0; i < rows; i++)
	{
		// Procesa cada columna del registro    
		cols = getEncabezados().length;

	    for (var j = 0; j < cols; j++) {
			value = eval("getRows()[" + i + "]." + getEncabezados()[j]);

			fetchValue(i,j,value);			
			//alert("value="+value);
		}
		fetchRow(i);			
	}
}

///////////////////////////////////////////////////////////////////////////////

function noFetchCol (i,j,value) {
	//alert ('fetch-col['+i+','+j+']='+value);
}

///////////////////////////////////////////////////////////////////////////////

function noFetchRow (i)
{
	//alert ('fetch-row['+i+']');
}

///////////////////////////////////////////////////////////////////////////////
// MANEJO DE URL
///////////////////////////////////////////////////////////////////////////////

function getURLParameter( paramName ) {
  paramName = paramName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&]"+paramName+"=([^&#]*)";
  var regex = new RegExp( regexS );
  var results = regex.exec( window.location.href );
  if( results == null )
    return "";
  else
    return results[1];
}

///////////////////////////////////////////////////////////////////////////////

function URLEnc(text) {
	var nocodificar = "0123456789"+"ABCDEFGHIJKLMNOPQRSTUVWXYZ"+"abcdefghijklmnopqrstuvwxyz" +"-_.!~*'()";
	var HEX = "0123456789ABCDEF";
	var textoAcodificar = text;
	var codificado = "";
	for (var i = 0; i < textoAcodificar.length; i++ ) {
		var ch = textoAcodificar.charAt(i);
	    if (ch == " ") {
		    codificado += "+";
		} else if (nocodificar.indexOf(ch) != -1) {
		    codificado += ch;
		} else {
		    var charCode = ch.charCodeAt(0);
			if (charCode > 255) {
			    alert( "Caracter Unicode '"+ch+"' no puede ser codificado utilizando la codificacion URL estandar.\n" +
				          "(solo soporta caracteres de 8-bit.)\n" +
						  "Sera sustituido por un simbolo de suma (+)." );
				codificado += "+";
			} else {
				codificado += "%";
				codificado += HEX.charAt((charCode >> 4) & 0xF);
				codificado += HEX.charAt(charCode & 0xF);
			}
		}
	}
	return codificado;
}

///////////////////////////////////////////////////////////////////////////////

function URLDec(text){
   var HEXCHARS = "0123456789ABCDEFabcdef"; 
   var codificado = text;
   var textoAcodificar = "";
   var i = 0;
   while (i < codificado.length) {
       var ch = codificado.charAt(i);
	   if (ch == "+") {
	       textoAcodificar += " ";
		   i++;
	   } else if (ch == "%") {
			if (i < (codificado.length-2) 
					&& HEXCHARS.indexOf(codificado.charAt(i+1)) != -1 
					&& HEXCHARS.indexOf(codificado.charAt(i+2)) != -1 ) {
				textoAcodificar += unescape( codificado.substr(i,3) );
				i += 3;
			} else {
				alert( 'Bad escape combination near ...' + codificado.substr(i) );
				textoAcodificar += "%[ERROR]";
				i++;
			}
		} else {
		   textoAcodificar += ch;
		   i++;
		}
	} 
   return textoAcodificar;
}

///////////////////////////////////////////////////////////////////////////////
// MOSTRAR/OCULTAR CAMPOS (O SECCIONES)
///////////////////////////////////////////////////////////////////////////////

function disableElements() {
	var elemTypes = new Array("text", "input", "option", "textarea", "select-multiple", "button", "radio", "select-one", "image");
	var myElements = document.getElementsByTagName("*");

	for (i=0; i<myElements.length; i++) {
		var unTipo = myElements[i].type;
		if (unTipo != undefined &&
			unTipo != "hidden"  &&
			unTipo != "") 
		{
			for (j=0; j<elemTypes.length; j++) {
				if (unTipo == elemTypes[j]) 
				{ 
					myElements[i].disabled = true;
					break;
				}
			}
		}
	}
}

///////////////////////////////////////////////////////////////////////////////
// MANEJO DE ERRORES
///////////////////////////////////////////////////////////////////////////////

function fnSendException(eNumber, eName, eMessage, eDescription)
{
    var exceptionObj;
    exceptionObj=new Object();
	exceptionObj.number=eNumber;
	exceptionObj.name=eName;
	exceptionObj.message=eMessage;
	exceptionObj.description=eDescription;
	alert("Error de validacion...!\r\r"+ eDescription)
	//throw exceptionObj;
}

///////////////////////////////////////////////////////////////////////////////
