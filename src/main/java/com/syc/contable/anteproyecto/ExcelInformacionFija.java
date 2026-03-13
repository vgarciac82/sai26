package com.syc.contable.anteproyecto;

import java.util.Map;

/**
 * Informacion complentaria de archivos excel. En ocaciones existen archivos
 * excel que contienen informacion constante en cada renglon. Esta informacion
 * no se encuentra en la base de datos por lo que es necesario agregarla
 * manualmente
 * 
 * @author Vicente Garcia
 * 
 */
public class ExcelInformacionFija {

	private String				formato;
	private Map<String, Object>	informacionFija;
}
