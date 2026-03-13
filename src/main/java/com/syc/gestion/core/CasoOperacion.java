package com.syc.gestion.core;


import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.syc.gestion.util.Util;


public class CasoOperacion implements Serializable {

	public static final int		CREATED				= 1;
	public static final int		EXECUTED			= 2;
	public static final int		MSG_SENDED			= 4;

	private static final long	serialVersionUID	= 1L;

	private int					id_caso;
	private int					id_caso_oper;
	private int					id_tc;
	private int					id_oper;
	private Timestamp			co_fecha_ini;
	private int					co_tiempo_limite;
	private String				co_responsable;
	private String				co_observacion;
	private int					co_status			= -1;
	private String				user01;						// user01 - Tipo de
															// Instruccion
	private String				user02;						// user02 - Detalle
															// de Instruccion
	private String				user03;						// user03 - Fecha
															// Limite de
															// Atencion
	private String				user04;						// user04 - Asunto
															// Modificado
															// (Observaciones)
	private String				user05;						// user05 - Fecha de
															// Prorroga
	private String				user06;						// user06 - Motivo
															// de Prorroga
	private String				user07;						// user07 - Folio de
															// Respuesta
	private String				user08;						// user08 -
															// Respuesta
	private String				user09;						// user09 -
															// Coordinador
	private String				user10;						// user10 - Motivo
															// del Rechazo
	private String				user11;						// user11 - Estatus
															// de la Prorroga
	private String				user12;						// user12 - Nombres
															// de los Turnados
	private String				user13;						// user13 -
															// Disponible
	private String				user14;						// user14 -
															// Disponible
	private String				user15;						// user15 -
															// Disponible
	private String				o_importe;
	private String				o_status;
	private String				o_sicop;
	private String				o_map;
	private String				o_cal;
	private String				o_tipoAdecuacion;
	private String				o_nivelAdecuacion;
	private String				o_documento;
	private String				o_fechaAppCont;
	private String				firmaElectrionica;

	public String getfechaAppCont() {
		return o_fechaAppCont;
	}

	public void setfechaAppCont( String oFechaAppCont ) {
		o_fechaAppCont = oFechaAppCont;
	}

	public String getDocumento() {
		return o_documento;
	}

	public void setDocumento( String oDocumento ) {
		o_documento = oDocumento;
	}

	public String getTipoAdecuacion() {
		return o_tipoAdecuacion;
	}

	public void setTipoAdecuacion( String oTipoAdecuacion ) {
		o_tipoAdecuacion = oTipoAdecuacion;
	}

	public String getNivelAdecuacion() {
		return o_nivelAdecuacion;
	}

	public void setNivelAdecuacion( String oNivelAdecuacion ) {
		o_nivelAdecuacion = oNivelAdecuacion;
	}

	public void setFolioMap( String o_map ) {

		this.o_map = o_map;
	}

	public String getFolioMap() {

		return o_map;
	}

	public void setFolioSicop( String o_sicop ) {

		this.o_sicop = o_sicop;
	}

	public String getFolioSicop() {

		return o_sicop;
	}

	public void setFolioCal( String o_cal ) {

		this.o_cal = o_cal;
	}

	public String getFolioCal() {

		return o_cal;
	}

	public void setImporte( String o_importe ) {

		this.o_importe = o_importe;
	}

	public String getImporte() {

		return o_importe;
	}

	public void setStatusC( String o_status ) {

		this.o_status = o_status;
	}

	public String getStatusC() {

		return o_status;
	}

	public String getUser01() {
		return user01;
	}

	public void setUser01( String user01 ) {
		this.user01 = user01;
	}

	public String getUser02() {
		return user02;
	}

	public void setUser02( String user02 ) {
		this.user02 = user02;
	}

	public String getUser03() {
		return user03;
	}

	public void setUser03( String user03 ) {
		this.user03 = user03;
	}

	public String getUser04() {
		return user04;
	}

	public void setUser04( String user04 ) {
		this.user04 = user04;
	}

	public String getUser05() {
		return user05;
	}

	public void setUser05( String user05 ) {
		this.user05 = user05;
	}

	public String getUser06() {
		return user06;
	}

	public void setUser06( String user06 ) {
		this.user06 = user06;
	}

	public String getUser07() {
		return user07;
	}

	public void setUser07( String user07 ) {
		this.user07 = user07;
	}

	public String getUser08() {
		return user08;
	}

	public void setUser08( String user08 ) {
		this.user08 = user08;
	}

	public String getUser09() {
		return user09;
	}

	public void setUser09( String user09 ) {
		this.user09 = user09;
	}

	public String getUser10() {
		return user10;
	}

	public void setUser10( String user10 ) {
		this.user10 = user10;
	}

	public String getUser11() {
		return user11;
	}

	public void setUser11( String user11 ) {
		this.user11 = user11;
	}

	public String getUser12() {
		return user12;
	}

	public void setUser12( String user12 ) {
		this.user12 = user12;
	}

	public String getUser13() {
		return user13;
	}

	public void setUser13( String user13 ) {
		this.user13 = user13;
	}

	public String getUser14() {
		return user14;
	}

	public void setUser14( String user14 ) {
		this.user14 = user14;
	}

	public String getUser15() {
		return user15;
	}

	public void setUser15( String user15 ) {
		this.user15 = user15;
	}

	private Operacion	operacion;
	private String		operador;

	public int getIdCaso() {
		return id_caso;
	}

	public void setIdCaso( int id_caso ) {
		this.id_caso = id_caso;
	}

	public int getIdCasoOper() {
		return id_caso_oper;
	}

	public void setIdCasoOper( int id_caso_oper ) {
		this.id_caso_oper = id_caso_oper;
	}

	public int getIdTC() {
		return id_tc;
	}

	public void setIdTC( int id_tc ) {
		this.id_tc = id_tc;
	}

	public int getIdOperacion() {
		return id_oper;
	}

	public void setIdOperacion( int id_oper ) {
		this.id_oper = id_oper;
	}

	public Timestamp getFechaInicio() {
		return co_fecha_ini;
	}

	public String getFormatFechaInicio( String format ) {
		return ( new SimpleDateFormat( format ) ).format( co_fecha_ini );
	}

	public void setFechaInicio( Timestamp co_fecha_ini ) {
		this.co_fecha_ini = co_fecha_ini;
	}

	public int getTiempoLimite() {
		return co_tiempo_limite;
	}

	public void setTiempoLimite( int co_tiempo_limite ) {
		this.co_tiempo_limite = co_tiempo_limite;
	}

	public String getResponsable() {
		return co_responsable;
	}

	public void setResponsable( String co_responsable ) {
		this.co_responsable = co_responsable;
	}

	public String getObservacion() {
		return co_observacion;
	}

	public void setObservacion( String co_observacion ) {
		this.co_observacion = co_observacion;
	}

	public int getStatus() {
		return co_status;
	}

	public void setStatus( int co_status ) {
		this.co_status = co_status;
	}

	public Operacion getOperacion() {
		return operacion;
	}

	public void setOperacion( Operacion operacion ) {
		this.operacion = operacion;
	}

	public Timestamp getFechaTiempoLimite() {
		if ( co_tiempo_limite > -1 )
			return Util.agregaTiempo( getFechaInicio(), getTiempoLimite(), Calendar.SECOND );

		return Util.agregaTiempo( new Timestamp( System.currentTimeMillis() ), 1, Calendar.DATE );
	}

	public boolean alcanzoTiempoLimite() {
		return Util.alcanzoTiempoLimite( new Timestamp( System.currentTimeMillis() ), getFechaTiempoLimite() );
	}

	public int getTiempoPromedio() {
		return ( int ) Util.getPromedioTimestamp( getFechaInicio(), new Timestamp( System.currentTimeMillis() ), getFechaTiempoLimite() );
	}

	public String getFirmaElectrionica() {
		return firmaElectrionica;
	}

	public void setFirmaElectrionica( String firmaElectrionica ) {
		this.firmaElectrionica = firmaElectrionica;
	}

	/**
	 * Establece el operador de la operacion
	 * 
	 * @param operador
	 */
	public void setOperador( String operador ) {
		this.operador = operador;
	}

	/**
	 * Devuelve el operador de la operacion
	 * 
	 * @return Operador
	 */
	public String getOperador() {
		return this.operador;
	}

}
