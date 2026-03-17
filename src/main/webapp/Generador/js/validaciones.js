// JavaScript Document
function valFmt(Objeto, Tipo, aMayusc)
{
		/*
		::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
		:: Verifica los caracteres capturados                                   
        ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
		*/
			var LongitudValor = Objeto.value.length +1
			var SubCadena = String.fromCharCode(window.event.keyCode);
			var PintarCar209 = true; //
			var Cadena = ""
			var LetrasMin = String.fromCharCode(225, 233, 237, 243, 250, 241);
			var LetrasMay = String.fromCharCode(193, 201, 205, 211, 218, 209);

			switch(Tipo){
				case 1:  //Letras
					var cadStr = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZñáéíóú-_/() ' + LetrasMay;
				break;
				case 2: //Números
					var cadStr = '0123456789'
				break;
				case 3:  //Letras y Números
					var cadStr = ' 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ.-_ñáéíóú/()' + LetrasMay;
				break;
				case 4:  
					var cadStr = ' #&@0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz.-_/()%' + LetrasMay + LetrasMin;
				break;
				case 5:  //Hora
					var cadStr = '0123456789:'
				break;
				case 6:  //Calendario de carga
					var cadStr =  '0123456789,-* '
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
				case 10: //Tipo Boolean  Bit
					var cadStr = '01'
				break;
				case 11:  //Letras y Números sin espacio
					var cadStr = '0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz.-_/()' + LetrasMay + LetrasMin;
				break;
				case 12:  //No editable
					var cadStr = '';
				break;
				case 13:  //Letras y Números sin espacio
					var cadStr = '012345678???????E???????????????????????????????????????????????????????????????????????	??E?????????????????????????????????????????????????????????????????????????????????????9AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz';
				break;
				case 14: //Horas
 					var cadStr = '0123456789:'
				break;
				case 15:  //Concepto
					var cadStr = ' 0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz.-_/()' + LetrasMay + LetrasMin;
				break;
				case 19: //Números Decimales positivos
					var cadStr = '0123456789.'
					break;
				case 20: //Números con signo
					var cadStr = '0123456789-'
				break;
				case 100: //Horas
 					var cadStr = ''
				break;
				
			}
		if (SubCadena.charCodeAt(0) != 16 && SubCadena.charCodeAt(0) != 37) 
		{
			if (LongitudValor>0){
			   for (i=1; i<=cadStr.length; i++)
			   {
					if (cadStr.charCodeAt(i-1)==SubCadena.charCodeAt(0) )
						{
							Cadena=cadStr.substring(i,i-1);
							i=cadStr.length;
						}
			   }
			   
			   if (Cadena.length==0){
			      window.event.keyCode=0
			   }
			   
			}
	}
}
function valFecha(object1) 
{
    if (object1.value != "") 
	{
      if (Verifica_Fecha(object1.value) == false) 
      {
         object1.value = "";
         alert("Formato de fecha incorrecto. Debe ser DD/MM/YYYY");
      }
   }
}

    function Verifica_Fecha(pstrFecha)
	    {
        // ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        // :: Proposito : Verifica si pstrFecha, contiene un formato y fecha ::
        // ::             correcta, del tipo dd/mm/aaaa                      ::
        // :: Entradas  : pstrFecha, string a validar                        ::
        // ::                                                                ::
        // :: :
        // ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        
        var strCharCorrectos = "0123456789/";
        var Fecha = pstrFecha;
	    if (Fecha == "")
		    return false;
	
	    for (i=0; i < Fecha.length;i++)
    		{   var car = Fecha.substr(i,1);
			    if (strCharCorrectos.indexOf(car)==-1)
			        {
				      return false;
				    }
		    }
	    
	    /* ::::::::::::::::::::::::::::::::::::::::::::::
	       Creamos un arreglo con los datos de la fecha 
	       separados por la diagonal
	       ::::::::::::::::::::::::::::::::::::::::::::::
	    */ dd=0
	       mm=1
	       aaaa=2
	    ArrayFecha = Fecha.split("/");
	    if(ArrayFecha.length!=3)
    	    return false;
    
	    var Dia  = Number(ArrayFecha[0]);
	    var Mes  = Number(ArrayFecha[1]);
	    var Anno = Number(ArrayFecha[2]);
	    var TemAno =String(ArrayFecha[2]);
	    
    	
    	if (TemAno.length != 4 )  return false;
    	if (Dia > 31 || Dia < 1 ) return false;
    	if (Mes >12 || Mes < 1 )  return false;
    	if (Mes == 4 || Mes == 6 || Mes == 9 || Mes == 11)
    	    {
	        if (Dia > 30 ) return false;
	        }	
	    if ( Mes == 2 )
	        {
    	    if ((Anno % 4)==0 )  /* Se verifica si el Anno es biciesto **/
			    { 
			    if (Dia > 29 ) return false;				
			    }
		    else{
				 if (Dia > 28) return false;
				}
            }
    
        return true;
	
        }

function addCommas(nStr){
            nStr += '';
            x = nStr.split('.');
            x1 = x[0];
            x2 = x.length > 1 ? '.' + x[1] : '';
            var rgx = /(\d+)(\d{3})/;
            while (rgx.test(x1)) {
               x1 = x1.replace(rgx, '$1' + ',' + '$2');
            }
            return x1 + x2.substring(0,3);
        }
        
        
function validarCaracteres(e) {
		var regex = new RegExp("^[ 0-9A-Za-zÑñáéíóúÁÉÍÓÚ.,-_/]+$");
		  var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
		  if (!regex.test(key)) {
		    e.preventDefault();
		    return false;
		 }
	}


 function quitaFmt( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(",", "");
	   	val = val.replace(",", "");
	   	val = val.replace(",", "");
	   	val = val.replace(",", "");
	   	val = val.replace(/,/g, "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val
	}
       
 function Sinfrmt(fld) {
		var valcol = $(fld).val();
		valcol = valcol.replace("$", "");
		valcol = valcol.replace(",", "");
		$(fld).val(valcol);
	}

function cambiafrmt(fld) {	

		$(fld).formatCurrency();
	}
	
	/**
	 * Funcion general que limpia el contenido de un select agregando una opcion por
	 * default con valor -1
	 */
	function clearSelect(idSel) {
		for ( var i = 0; i < idSel.length; i++)
			$('#' + idSel[i]).find('option').remove().end().append(
					'<option value="-1"></option>');
	}

	
function currencyFormatter({ currency, value}) {
  const formatter = new Intl.NumberFormat('en-US', {
    style: 'currency',
    minimumFractionDigits: 2,
    currency
  }) 
  return formatter.format(value)
}
	