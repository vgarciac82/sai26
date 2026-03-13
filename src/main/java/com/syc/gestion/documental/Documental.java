package com.syc.gestion.documental;

import java.io.Serializable;

public class Documental implements Serializable{

	private final static long serialVersionUID = 1;
	// 1. Constantes para el Tipo de Operacion
	//    Validas solo para la version Documental de Gestion
	//    Corresponde a los registros de la tabla CG_OPERACION
	//    NOTA: Si se modifican los registros de CG_OPERACION, deben modificarse estas constantes y viceversa
	public static final int OPR_CONTINUAR                = 0;
	
	public static final int OPR_RECEPCION                = 1;
	public static final int OPR_ATENCION                 = 2;
	public static final int OPR_TURNADO                  = 3;
	//GAF
	public static final int OPR_RESPUESTA_TOTAL          = 4;
	public static final int OPR_RESPUESTA_PARCIAL        = 13;
	
	public static final int OPR_POR_CERRAR               = 5;
	public static final int OPR_RECHAZAR                 = 6;
	public static final int OPR_COPIA_PARA               = 7;
	public static final int OPR_PRORROGA                 = 8;
	public static final int OPR_RECHAZO_PRORROGA         = 9;
	public static final int OPR_RECHAZO_RESPUESTA        = 10;
	public static final int OPR_ACEPTAR_PRORROGA         = 11;
	public static final int OPR_RESPUESTA_COORD          = 12;

	public static final String OPR_NOM_TERMINAR          = "TERMINAR";
	public static final String OPR_NOM_CONTINUAR         = "CONTINUAR";
	
	public static final String OPR_NOM_RECEPCION         = "RECEPCION";
	public static final String OPR_NOM_ATENCION          = "ATENCION";
	public static final String OPR_NOM_TURNADO           = "TURNADO";

	public static final String OPR_NOM_RESPUESTA_TOTAL   = "RESPUESTA_TOTAL";
	public static final String OPR_NOM_RESPUESTA_PARCIAL = "RESPUESTA_PARCIAL";
	
	public static final String OPR_NOM_POR_CERRAR        = "POR_CERRAR";
	public static final String OPR_NOM_RECHAZAR          = "RECHAZAR";
	public static final String OPR_NOM_COPIA_PARA        = "COPIA_PARA";
	public static final String OPR_NOM_PRORROGA          = "PRORROGA";
	public static final String OPR_NOM_RECHAZO_PRORROGA  = "RECHAZO_PRORROGA";
	public static final String OPR_NOM_RECHAZO_RESPUESTA = "RECHAZO_RESPUESTA";
	public static final String OPR_NOM_ACEPTAR_PRORROGA  = "ACEPTAR_PRORROGA";
	public static final String OPR_NOM_RESPUESTA_COORD   = "RESPUESTA_COORD";	

	// 2. Constantes para el Manejo del Stack para las Operaciones
	//    Corresponde a los registros de la tabla CG_OPERACION
	//    NOTA: Si se modifican los registros de CG_OPERACION, deben modificarse estas constantes y viceversa
	public static final int OPR_STACK_PUSH = +1;
	public static final int OPR_STACK_POP  = -1;
	public static final int OPR_STACK_NOP  =  0;
	
	public static final int[] OPR_STACK_LST = 
		{ OPR_STACK_NOP		// OPR_CONTINUAR
		, OPR_STACK_PUSH	// OPR_RECEPCION
		, OPR_STACK_PUSH	// OPR_ATENCION
		, OPR_STACK_NOP		// OPR_TURNADO
		, OPR_STACK_POP		// OPR_RESPUESTA_TOTAL
		, OPR_STACK_POP		// OPR_POR_CERRAR
		, OPR_STACK_POP		// OPR_RECHAZAR
		, OPR_STACK_NOP		// OPR_COPIA_PARA
		, OPR_STACK_POP		// OPR_PRORROGA
		, OPR_STACK_PUSH	// OPR_RECHAZO_PRORROGA
		, OPR_STACK_PUSH	// OPR_RECHAZO_RESPUESTA
		, OPR_STACK_PUSH	// OPR_ACEPTAR_PRORROGA
		, OPR_STACK_NOP		// OPR_RESPUESTA_COORD
		, OPR_STACK_POP		// OPR_RESPUESTA_PARCIAL
		};
	
	// 3. Constantes para el Tipo de Accion
	//    Validas solo para la version Documental de Gestion
	//    Corresponde a los botones de la pantalla (asunto.jsp)
	public static final int ACC_REGISTRAR                = 1;
	public static final int ACC_TURNAR                   = 2;
	public static final int ACC_CONCLUIR                 = 3; 
	public static final int ACC_RECHAZAR_RESPONSABILIDAD = 4;
	public static final int ACC_RECHAZAR_RESPUESTA       = 5;
	public static final int ACC_SOLICITAR_PRORROGA       = 6; 
	public static final int ACC_RECHAZAR_PRORROGA        = 7;
	public static final int ACC_ACEPTAR_PRORROGA         = 8;
	//GAF 2009-08-17
	public static final int ACC_ACEPTAR_RESPUESTA        = 9;

	// 4. Constantes para el Estatus de la Prorroga
	public static final String PRR_ACEPTAR				 = "A";
	public static final String PRR_RECHAZAR				 = "R";
	public static final String PRR_SOLICITAR			 = "S";

	// 5. Otras constantes
	public static final String DATE_FORMAT_NOW = "dd/MM/yyyy";

	public static final int BD_OPR_SELECT                = 1;
	public static final int BD_OPR_INSERT                = 2;
	public static final int BD_OPR_UPDATE                = 3;
	public static final int BD_OPR_DELETE                = 4;
}