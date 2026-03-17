
String.prototype.Capitalize = function() {
	return this.replace(/(^|\s)([a-z])/g, function(m, p1, p2) {
		return p1 + p2.toUpperCase();
	});
};

function Dig_Izq(valor) {
	//alert("DIG");
	valor = new String(valor);
	if (valor.length == 1)
		valor = "0" + valor;
	return valor;
}

function cerosIzq(sVal, nPos) {
	//alert(sVal);
	var sRes = sVal;
	for (var i = sVal.length; i < nPos; i++)
		sRes = "0" + sRes;
	return sRes;
}

function DameFechaHoy() {
	Avui = new Date();
	diaSet = Avui.getDay();

	if (diaSet == 0) {
		diaSet = "Domingo"
	} else {
		if (diaSet == 1) {
			diaSet = "Lunes"
		} else {
			if (diaSet == 2) {
				diaSet = "Martes"
			} else {
				if (diaSet == 3) {
					diaSet = "Miercoles"
				} else {
					if (diaSet == 4) {
						diaSet = "Jueves"
					} else {
						if (diaSet == 5) {
							diaSet = "Viernes"
						} else {
							diaSet = "Sabado"
						}
					}
				}
			}
		}
	}

	diaMes = Avui.getDate();
	mes = Avui.getMonth() + 1;

	if (mes == 1) {
		mes = "Enero"
	} else {
		if (mes == 2) {
			mes = "Febrero"
		} else {
			if (mes == 3) {
				mes = "Marzo"
			} else {
				if (mes == 4) {
					mes = "Abril"
				} else {
					if (mes == 5) {
						mes = "Mayo"
					} else {
						if (mes == 6) {
							mes = "Junio"
						} else {
							if (mes == 7) {
								mes = "Julio"
							} else {
								if (mes == 8) {
									mes = "Agosto"
								} else {
									if (mes == 9) {
										mes = "Septiembre"
									} else {
										if (mes == 10) {
											mes = "Octubre"
										} else {
											if (mes == 11) {
												mes = "Noviembre"
											} else {
												mes = "Diciembre"
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	any = Avui.getYear();
	if (any < 2000) {
		any = any + 1900;
	}

	return diaSet + ", " + diaMes + " de " + mes + " del " + any;
}

/* 
	=====================================================================
	=== Proposito:	Este archivo contiene funciones para validar Fechas,
	===				varificar numeros, cade3nas vacias, trasformar un XML
	=== Programador: 
	=== E-mail:		
	=====================================================================
*/

var objFecha; //
var strFecha; //Esta variable es moficada desde el calendario

//Deshabiltamos el menu contextual del browser
//document.onmousedown = MouseDown;	
function MouseDown() {
	Boton = event.button;
	if (Boton == 2) {
		alert('Por politicas de seguridad el codigo de esta pagina no puede ser mostrado');
	}

}
function MuestraDatos(pXMLDoc, pXSLDoc, pDivHTML) { /* 
	    ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
		::	Parameros:	XML ,XSL, DIVHTML                                   ::
		::				XML es el objeto que contiene los datos en XML      ::
		::				XSL es el objeto que tiene el Stilo para los datos  ::
		::				DIVHTML es el objeto donde se mostrara el resultado ::
		::				de la mezcla entre el XML y XSL                     ::
		::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
		*/
	/* ::::: Asignamos los Documentos a Variables ::::: */
	ObjXML = document.all(pXMLDoc);
	ObjXSL = document.all(pXSLDoc);
	ObjDIV = document.all(pDivHTML);

	//alert(ObjXML.xml);
	//alert(ObjXSL.xml);

	ObjXML.preserveWhiteSpace = false;
	ObjXSL.preserveWhiteSpace = false;

	/* ::::: Identificamos si los Documentos (XML y XSL) estan bien formateados :::: */
	if (ObjXML.parseError.reason != "") alert("xml: " + ObjXML.parseError.reason);
	if (ObjXSL.parseError.reason != "") alert("xsl: " + ObjXSL.parseError.reason);

	/*  :::: Verificamos si contiene el mensaje de Error :::: */
	objNodeList = ObjXML.getElementsByTagName("ERROR");
	if (objNodeList.length > 0) alert(objNodeList.item(0).nodeTypedValue);

	/*  :::: Verificamos si se Termino la Sesion :::: */
	objNodeList = ObjXML.getElementsByTagName("FINDESESION");
	if (objNodeList.length > 0)
		self.location = objNodeList.item(0).nodeTypedValue;


	/* :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	   :: Realizamos la mezcla entre XML y XSL, y el resultado 
	   :: se inserta en el Div Indicado 
	   :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	*/

	ObjDIV.innerHTML = ObjXML.transformNode(ObjXSL.XMLDocument);
	//alert(ObjDIV.innerHTML);
	return true;
}



function Obliga(Dato, Mensaje) {
	// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	// :: Proposito : Verifica Que el campo no este vacio o nulo         ::
	// :: Entradas  : cualquier campo                         ::
	// :: Salidad   : True (Válida) o False (Incorrecta)                 ::
	// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	var Campo = Dato.value;
	var puntero = Dato

	if (Campo == "" || Campo.length == 0 || Campo == null) {
		alert(Mensaje);
		puntero.focus()
		return false;
	}

	else return true;


}

function LeePermiso(pPermiso) {
	/* 
	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	:: Obtiene el Valor del Permiso que se Encuentra en La Pagina Principal ::::
	:: pPermiso = Permiso a Obtener su valor                                ::::
	:: Regresa el Valor del Permiso, En caso de Error lo indica y regresa 0 ::::
	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	*/
	permiso = 'parent.top.' + pPermiso;
	permiso = eval(permiso);
	if (isNaN(permiso)) {
		alert('Permiso: ' + pPermiso + ' no definido');
		return 0;
	}
	else return permiso;

}

function EsNumero(pValor, pstrMensaje) { /* Verifica si el parametro indicado es un Número */
	if (isNaN(pValor) == true || pValor == "") {
		if (pstrMensaje != "") alert(pstrMensaje);
		return false;
	}

	else return true;
}




function Convierte_a_Numero(pValor) {
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//:: Quita las comas que puediera tener un Número, ya que 
	//:: en JavaScript los números que contiene comas son tratados
	//:: como cadenas de caracteres y no como tales
	//::
	//:: Entradas : pValor es el número al cual se le van a quitar
	//::            las comas.
	//:: Salidas  : Regresa el Nuevo string con el Número pero sin
	//::            comas.
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	var dblValorSinComas = "";
	if (pValor.indexOf(",") >= 0) //:: Se verifica si el Numero biene con comas
	{
		pValor = pValor.split(","); //:: Creamos un arreglo de datos, separados por comas
		items = pValor.length;
		for (y = 0; y < items; y++)
			dblValorSinComas = dblValorSinComas + pValor[y];

	}
	else
		dblValorSinComas = pValor;

	return dblValorSinComas; //:: Regresamos el Número pero sin comas

}

function strTrim(pStr) {
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//:: Esta función quita espacios tanto a la Izquierda como a  ::
	//:: la derecha de una cadena de caracteres, parecida a la    ::
	//:: función de Visual Basic TRIM.                            ::
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	var Len = pStr.length;
	while (pStr.charAt(0) == " ") {
		pStr = pStr.substring(1);
	}

	Len = pStr.length;

	while (pStr.charAt(Len - 1) == " ") {
		pStr = pStr.substring(0, Len - 1);
		Len = pStr.length;
	}
	return pStr;

}

function EsMayordeCero(pValor, strMensaje) {
	if (pValor > 0) return true;

	alert(strMensaje);
	return false;
}

function EstaVacio(pObj, strMensaje) {
	if (pObj.value != "") return true;

	if (strMensaje != "") alert(strMensaje);
	pObj.focus();

	return false;
}

function ElementoSeleccionado(pObj, strMensaje) {
	if ((pObj.length == 0) || (pObj.options[pObj.selectedIndex].value == -1) || (pObj.options[pObj.selectedIndex].value == "")) {

		if (strMensaje != "") {
			alert(strMensaje);
			pObj.focus();
		}
		return false;

	}
	return true;
}

function Verifica_Fecha(pstrFecha) {
	// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	// :: Proposito : Verifica si pstrFecha, contiene un formato y fecha ::
	// ::             correcta, del tipo dd/mm/aaaa                      ::
	// :: Entradas  : pstrFecha, string a validar                        ::
	// :: Salidad   : True (Válida) o False (Incorrecta)                 ::
	// ::                                                                ::
	// :: :
	// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	var strCharCorrectos = "0123456789/";
	var Fecha = pstrFecha;
	if (Fecha == "")
		return false;

	for (i = 0; i < Fecha.length; i++) {
		var car = Fecha.substr(i, 1);
		if (strCharCorrectos.indexOf(car) == -1) {
			return false;
		}
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::
	   Creamos un arreglo con los datos de la fecha 
	   separados por la diagonal
	   ::::::::::::::::::::::::::::::::::::::::::::::
	*/
	dd = 0
	mm = 1
	aaaa = 2
	ArrayFecha = Fecha.split("/");
	if (ArrayFecha.length != 3)
		return false;

	var Dia = Number(ArrayFecha[0]);
	var Mes = Number(ArrayFecha[1]);
	var Anno = Number(ArrayFecha[2]);
	var TemAno = String(ArrayFecha[2]);


	if (TemAno.length != 4) return false;
	if (Dia > 31 || Dia < 1) return false;
	if (Mes > 12 || Mes < 1) return false;
	if (Mes == 4 || Mes == 6 || Mes == 9 || Mes == 11) {
		if (Dia > 30) return false;
	}
	if (Mes == 2) {
		if ((Anno % 4) == 0) /* Se verifica si el Anno es biciesto **/ {
			if (Dia > 29) return false;
		} else {
			if (Dia > 28) return false;
		}
	}

	return true;

}

/*	
	Esta función selecciona el contenido de una caja de texto
	recibe como parametros el objeto a seleccionar
*/
function Selecciona(obj) {
	obj.select();
}

/*
	Esta función muestra el calendario para seleccionar una fecha
	Recibe como parametro el nombre del objeto sobre el cual se regresara la 
	fecha seleccionada
*/
//	function MuestraCalendario(obj)
function muestraCalendario(obj) {
	var strURL = "calendario.htm";
	var winCalendario = window.showModalDialog(strURL, self, "dialogHeight: 225px; dialogWidth: 270px; center: Yes; resizable: no; status: no;");
	if (strFecha != undefined)
		obj.value = strFecha; //document.all[objFecha].value = strFecha
	strFecha = undefined;
}

/*
	Esta función Válida que los datos capturen en la caja de texto
	sea un valor numérico correcto, si no es así, le asigna el valor de cero
	y lo enfoca
	regresa true o false		
*/
function Valida_Monto(obj) {
	var Monto = obj.value;
	if (EsNumero(Monto, "") == false) {
		alert('Por favor indique una cantidad correcta');
		obj.value = '0';
		obj.focus();
		return false;
	}
	return false;

}

function Redondea(obj) {
	if (EsNumero(obj.value, "") == true)
		obj.value = RedondeaNumero(obj.value)
	else
		obj.value = 0;
}

function Ucase(pObj) {
	var str = pObj.value;
	pObj.value = str.toUpperCase();
}

function ComparaFechas(pFecha1, pFecha2) {
	// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	// :: Proposito : Realizar la comparación de dos fechas              ::
	// :: Entradas  : pFecha1 y pFecha2  fechas a comparar               ::
	// :: Salidad   : igual, menor, mayor o Error en fechas 			 ::
	// ::                                                                ::
	// :: Programador : Israel Ochoa P. (OPI)   Fecha: 24 Abril 2002     ::
	// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	//Creamos los objetos de Tipo Fecha
	var pFechaIni = new Date();
	var pFechaFin = new Date();
	//Se verifica que las fechas tengan un formato correcto
	if ((Verifica_Fecha(pFecha1) == true) && (Verifica_Fecha(pFecha2) == true)) {
		//generamos un arreglo para separar el mes dia y año de los strings
		pFecha1 = pFecha1.split("/");
		pFecha2 = pFecha2.split("/");

		//A le indicamos a los objetos fecha el año, mes y dia
		pFechaIni.setFullYear(pFecha1[2], pFecha1[1], pFecha1[0]);
		pFechaFin.setFullYear(pFecha2[2], pFecha2[1], pFecha2[0]);

		//Se hace la comparación de las fechas
		if (pFechaIni.valueOf() == pFechaFin.valueOf()) return "igual";
		if (pFechaIni.valueOf() < pFechaFin.valueOf()) return "menor";
		if (pFechaIni.valueOf() > pFechaFin.valueOf()) return "mayor";

	} else {
		alert('Las fechas indicadas no son correctas');
		return "Error en fechas"
	}

}

function VerificaCaracteres(Cadena, CaracteresValidos) {
	var i = 0;
	for (i = 0; i < Cadena.length; i++) {
		if (CaracteresValidos.indexOf(Cadena.charAt(i)) < 0) return false;
	}
	return true;
}

function ValidarCaracteres(obj, CaracteresNoValidos) {
	var i = 0;
	var cadSeparada = "";

	for (i = 0; i < obj.value.length; i++) {
		if (CaracteresNoValidos.indexOf(obj.value.charAt(i)) > -1) {
			for (i = 0; i < CaracteresNoValidos.length; i++) {
				cadSeparada += CaracteresNoValidos.charAt(i) + " ";
			}

			alert("Caracteres no aceptados: \n" + cadSeparada);
			return false;
		}
	}
	return true;
}

function Validaciones(Objeto, Tipo) {
	/*
	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	:: Verifica los caracteres capturados                                   
		::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	*/
	var LongitudValor = Objeto.value.length + 1
	var SubCadena = String.fromCharCode(window.event.keyCode);
	var PintarCar209 = true; //Ñ
	var Cadena = ""
	var LetrasMin = String.fromCharCode(225, 233, 237, 243, 250, 241);
	var LetrasMay = String.fromCharCode(193, 201, 205, 211, 218, 209);


	//alert(SubCadena);
	switch (Tipo) {
		case 0: //Letras solo sin espacios
			var cadStr = 'AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz' + LetrasMay;
			break;
		case 1: //Letras
			var cadStr = 'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜ-_/() ' + LetrasMay;
			break;
		case 2: //Números
			var cadStr = '0123456789'
			break;
		case 3: //Letras y Números
			var cadStr = ' 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ.-_ÁáÉéÍíÓóÚúÜü/()' + LetrasMay;
			break;
		case 4: //Letras mayúsculas, minúsculas y Números
			var cadStr = ' @0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz.-_ÁáÉéÍíÓóÚúÜü/()%#' + LetrasMay + LetrasMin;
			break;
		case 5: //HoraÐ
			var cadStr = '0123456789:'
			break;
		case 6: //Calendario de carga
			var cadStr = '0123456789,-* '
			PintarCar209 = false;
			break;
		case 7: //Fechas
			var cadStr = '0123456789/'
			break;
		case 8: //Números telefonicos
			var cadStr = '0123456789-()EXT. '
			break;
		case 9: //Números Decimales
			var cadStr = '0123456789.-'
			break;
		// Tipos de datos BEPM27FEB2004
		case 10: //Tipo Boolean ó Bit
			var cadStr = '01'
			break;
		case 11: //Letras y Números sin espacio
			var cadStr = '0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz.-_ÁáÉéÍíÓóÚúÜü/()' + LetrasMay + LetrasMin;
			break;
		case 12: //No editable
			var cadStr = '';
			break;
		case 13: //Letras y Números sin espacio
			var cadStr = '0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz';
			break;
		case 14: //Letras mayusculas y Números sin espacio
			var cadStr = '0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz' + LetrasMay;
			break;
		case 15: //para mail
			var cadStr = '@0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz.-_' + LetrasMin;
			break;
		case 16: //Letras mayúsculas, minúsculas y Números
			var cadStr = ' 0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnÑñOoPpQqRrSsTtUuVvWwXxYyZz.ÁáÉéÍíÓóÚúÜü' + LetrasMay + LetrasMin;
			break;
		case 17: //Letras mayúsculas, minúsculas y Números c/espacio
			var cadStr = '0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz -' + LetrasMay + LetrasMin;
			break;
	}

	if (LongitudValor > 0) {
		for (i = 1; i <= cadStr.length; i++) {
			//alert(cadStr.substring(i,i-1) + '   code=' + cadStr.charCodeAt(i-1) + '   ' + SubCadena + ' Subcadena=' + SubCadena.charCodeAt(0));
			if (cadStr.charCodeAt(i - 1) == SubCadena.charCodeAt(0)) {
				Cadena = cadStr.substring(i, i - 1);
				i = cadStr.length;
			}
		}

		if (Cadena.length == 0) {
			//Objeto.value=Objeto.value.substring(0, Objeto.value.length-1);
			window.event.keyCode = 0
			//Objeto.focus();
		}

	}
}

function Mascara(ObjetoWin, Objeto, cadStr) {
	ObjetoWin.event.keyCode = String.fromCharCode(ObjetoWin.event.keyCode).toUpperCase().charCodeAt(0);
	var LongitudValor = Objeto.value.length + 1;
	var cad = cadStr.substring(LongitudValor, LongitudValor - 1);
	if (LongitudValor <= cadStr.length) {
		switch (cad) {
			case 'X': //Letras			
				Validaciones(Objeto, 6);
				break;
			case '#': //Números
				Validaciones(Objeto, 2);
				break;
			case 'A': //Letras y Números
				Validaciones(Objeto, 7);
				break;
		}
	} else {
		ObjetoWin.event.keyCode = 0
	}
}




function EscorreoCorrecto(pCampo, pstrMensaje) { /* Verifica si el correo es correcto */
	var Correo = pCampo.value;
	var Campo = pCampo;

	if (Correo.length == 0) return true;
	if (Correo.indexOf("@") < 0) {
		alert('Correo incorrecto falta(@)')
		return false;
	}

	if (Correo.indexOf(".") < 0) {
		alert('Correo incorrecto falta(.)')
		return false;
	}

	if (Correo.indexOf("@") == 0) {
		alert("'" + Correo + "'" + ' es incorrecto')
		Campo.focus;
		return false;
	}

	if (Correo.indexOf(" ") > 0) {
		alert("'" + Correo + "'" + ' es incorrecto')
		Campo.focus;
		return false;
	}

	return true;
}




// Realiza la pregunta "question" y regresa la respuesta "answer" (opcional)
// El Tercer argumento "condition" debe contener una expresion o valor boleano (true/false),
// para hacer o no la pregunta
// 1 - Afirmativo, 0 - Negativo
// Regresa: true/false
function fn_Confirmacion(question, answer, condition) {
	var retvar = true;
	var el = document.forms[0].elements; //obtiene los elementos de la pantalla
	var x = 0;

	for (x = 0; x < el.length; x++) {
		if (el[x].name == answer) {
			//alert(el[x].name); // obtiene el nombre del radio buttons.
			break;
		}
	}

	if (fn_Confirmacion.arguments.length == 3) {
		if (condition)
			retvar = confirm(question);
		if (retvar == true) {
			el[x].value = 0;
		}
		else
			el[x].value = 1;
	} else {
		retvar = confirm(question);
	}

	if (fn_Confirmacion.arguments.length >= 2) {
		if (answer = "") eval("document.all." + answer + ".value = (retvar   1 : 0)");
	}

	return retvar;
}

function ValidaCaracteresClave(Dato, strMensaje) {
	var Campo = Dato.value;
	var puntero = Dato
	for (i = 0; i < Campo.length; i++) {
		if ((Campo.charCodeAt(i) < 65 || Campo.charCodeAt(i) > 90) && (Campo.charCodeAt(i) < 48 || Campo.charCodeAt(i) > 57)) {
			//alert("Caracter Invalido '" + Campo.charAt(i) + "'");
			alert(strMensaje + Campo.charAt(i) + "'");
			puntero.focus()
			return false;
		}
	}
	return true;
}

function ValidaCaracteres(Dato, strMensaje) {
	var Campo = Dato.value;
	var puntero = Dato
	for (i = 0; i < Campo.length; i++) {
		if ((Campo.charCodeAt(i) < 65 || Campo.charCodeAt(i) > 90) && (Campo.charCodeAt(i) < 97 || Campo.charCodeAt(i) > 122) && (Campo.charCodeAt(i) < 48 || Campo.charCodeAt(i) > 57)) {
			if (Campo.charCodeAt(i) != 32) {
				alert(strMensaje + Campo.charAt(i) + "'");
				puntero.focus()
				return false;
			}
		}
	}
	return true;
}

function createCookie(name, value, days) {
	if (days) {
		var date = new Date();
		date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
		var expires = "; expires=" + date.toGMTString();
	}
	else var expires = "";
	document.cookie = name + "=" + value + expires + "; path=/";
}

function readCookie(name) {
	alert("hello");
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ') c = c.substring(1, c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
	}
	return null;
}

function eraseCookie(name) {
	createCookie(name, "", -1);
}

function formatCurrency(num) {
	num = num.toString().replace(/\$|\,/g, '');
	if (isNaN(num))
		num = "0";
	sign = (num == (num = Math.abs(num)));
	num = Math.floor(num * 100 + 0.50000000001);
	cents = num % 100;
	num = Math.floor(num / 100).toString();
	if (cents < 10)
		cents = "0" + cents;
	for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
		num = num.substring(0, num.length - (4 * i + 3)) + ',' + num.substring(num.length - (4 * i + 3));
	return (((sign) ? '' : '-') + '$' + num + '.' + cents);
}


/**
 * Funcion da formato de Moneda al campo recibe id de campo.
 */
function moneyFrmt(id) {
	try {
		$("#" + id).formatCurrency();
	} catch (ex) {
		alert("Error 0001js.\r\r" + ex.message
			+ "\r\rFavor de reportarlo al Administrador del Sistema.");
	}
}

/**
 * Funcion quita formato de Moneda
 * para operaciones
 * recibe fload.
 */
function Sinfrmt(fld) {
	fld = fld.replace("$", "");
	fld = fld.replace(/,/g, "");
	if (fld == "")
		fld = 0;
	return fld;
}


function quitaFmtObj(elObjeto) {
	var val = 0;
	val = elObjeto.value;
	//alert(val);
	val = val.replace("$", "");
	val = val.replace(",", "");
	val = val.replace(",", "");
	val = val.replace(",", "");
	val = val.replace(",", "");

	if (val.indexOf("(") >= 0) {
		val = val.replace("(", "");
		val = val.replace(")", "");
		val = "-" + val;
	}
	elObjeto.value = val;
}

function estyleReadOnly() {
	$('input[readonly]').each(function() {
		$(this).addClass("readOnly");
	});
}

function convertDatePicker() {
	$(".fecha").datepicker({
		changeMonth: true,
		changeYear: true,
		showOn: "button",
		dateFormat: "dd/mm/yy",
		buttonImage: "../Generador/images/calendar.gif",
		buttonImageOnly: true
	});
}

function convertMoney() {
	$(".money").each(function() {

		moneyFrmt($(this).attr("id"));

		$(this).focus(function() {
			quitaFmtObj($(this)[0]);
		});

		$(this).blur(function() {
			moneyFrmt($(this).attr("id"));
		});

	});
}

function onlyNumbers(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode
	var strCheck = '-0123456789.';
	var key = String.fromCharCode(keyPressed);
	if (strCheck.indexOf(key) == -1)
		return false; // Valida que sea numero y punto decimal
	return true
}

/**
 * Valida que las fechas esten en orden creciente. 
 * @returns true si y solo si la fecha inicial es menor a la fecha final.
 * @param idFechaInicio ID del input con la fecha inicial
 * @param idFechaFin ID del input con la fecha final
 */
function ordenFechas(idFechaInicio, idFechaFin) {
	var fechaInicioArr = $("#" + idFechaInicio).val().split("/");
	var fechaFinArr = $("#" + idFechaFin).val().split("/");
	
	var fechaInicio = new Date(fechaInicioArr[2],fechaInicioArr[1],fechaInicioArr[0]);
	var fechaFin = new Date( fechaFinArr[2],fechaFinArr[1],fechaFinArr[0]); 

	return fechaFin > fechaInicio;
}