package com.syc.gestion.core;


import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.syc.gestion.util.Util;


public class Caso implements Serializable {

	public static final int			CREATED				= 1;
	public static final int			EXECUTED			= 2;
	private static final Logger		log					= Logger.getLogger( Caso.class );
	public static final int			MSG_SENDED			= 4;
	/**
	 * 
	 */
	private static final long		serialVersionUID	= 9164298987672396344L;

	private BitacoraTotal			bitacora			= null;
	private String					c_alarma;
	private Timestamp				c_fecha_ini;
	private String					c_folio;
	private int						c_id_gabinete;
	private String					c_nom_equipoIni;
	private String					c_nom_equipoUser;
	private int						c_status			= -1;
	private int						c_tiempo_limite;

	private Map<String, CasoDato>	caso_dato			= new LinkedHashMap<>();
	private Vector<CasoOperacion>	caso_operacion		= new Vector<>();
	private Timestamp				fechaCompromiso;
	private int						id_caso;
	private int						id_tc;
	private TipoCaso				tc;

	public Caso( String folio ) {
		this.c_folio = folio;
	}

	public Caso( ) {
	}

	public boolean alcanzoTiempoLimite() {
		return Util.alcanzoTiempoLimite( new Timestamp( System.currentTimeMillis() ), getFechaTiempoLimite() );
	}

	public String getAlarma() {
		return c_alarma;
	}

	public BitacoraTotal getBitacora() {
		return bitacora;
	}

	public String getC_nom_equipoIni() {
		return c_nom_equipoIni;
	}

	public String getC_nom_equipoUser() {
		return c_nom_equipoUser;
	}

	public Map<String, CasoDato> getCasoDato() {
		return caso_dato;
	}

	public CasoDato getCasoDato( String name ) {
		return ( CasoDato ) caso_dato.get( name );
	}

	public Vector<CasoOperacion> getCasoOperacion() {
		return caso_operacion;
	}

	public CasoOperacion getCasoOperacion( int index ) {
		if ( ( caso_operacion.isEmpty() ) || ( caso_operacion.size() < index ) )
			return null;

		return ( CasoOperacion ) caso_operacion.get( index );
	}

	public Timestamp getFechaCompromiso() {
		return fechaCompromiso;
	}

	public Timestamp getFechaInicio() {
		return c_fecha_ini;
	}

	public Timestamp getFechaTiempoLimite() {
		if ( c_tiempo_limite > -1 )
			return Util.agregaTiempo( c_fecha_ini, c_tiempo_limite, Calendar.SECOND );

		return Util.agregaTiempo( new Timestamp( System.currentTimeMillis() ), 1, Calendar.DATE );
	}

	public String getFolio() {
		return c_folio;
	}

	public String getFormatFechaInicio( String format ) {
		log.trace( "Formateando fecha " + format );
		return ( new SimpleDateFormat( format ) ).format( c_fecha_ini );
	}

	public int getIdCaso() {
		return id_caso;
	}

	public int getIdGabinete() {
		return c_id_gabinete;
	}

	public int getIdTC() {
		return id_tc;
	}

	public int getStatus() {
		return c_status;
	}

	public int getTiempoLimite() {
		return c_tiempo_limite;
	}

	public int getTiempoPromedio() {
		return ( int ) Util.getPromedioTimestamp( c_fecha_ini, new Timestamp( System.currentTimeMillis() ), getFechaTiempoLimite() );
	}

	public TipoCaso getTipoCaso() {
		return tc;
	}

	public void setAlarma( String c_alarma ) {
		this.c_alarma = c_alarma;
	}

	public void setBitacora( BitacoraTotal bitacora ) {
		this.bitacora = bitacora;
	}

	public void setC_nom_equipoIni( String c_nom_equipoIni ) {
		this.c_nom_equipoIni = c_nom_equipoIni;
	}

	public void setC_nom_equipoUser( String c_nom_equipoUser ) {
		this.c_nom_equipoUser = c_nom_equipoUser;
	}

	public void setCasoDato( Map<String, CasoDato> caso_dato ) {
		this.caso_dato = caso_dato;
	}

	public void setCasoOperacion( CasoOperacion co ) {
		caso_operacion.add( co );
	}

	public void setCasoOperacion( Vector<CasoOperacion> caso_operacion ) {
		this.caso_operacion = caso_operacion;
	}

	public void setFechaCompromiso( Timestamp fecha_compromiso ) {
		this.fechaCompromiso = fecha_compromiso;
	}

	public void setFechaInicio( Timestamp c_fecha_ini ) {
		this.c_fecha_ini = c_fecha_ini;
	}

	public void setFolio( String c_folio ) {
		this.c_folio = c_folio;
	}

	public void setIdCaso( int id_caso ) {
		this.id_caso = id_caso;
	}

	public void setIdGabinete( int c_id_gabinete ) {
		this.c_id_gabinete = c_id_gabinete;
	}

	public void setIdTC( int id_tc ) {
		this.id_tc = id_tc;
	}

	public void setStatus( int c_status ) {
		this.c_status = c_status;
	}

	public void setTiempoLimite( int c_tiempo_limite ) {
		this.c_tiempo_limite = c_tiempo_limite;
	}

	public void setTipoCaso( TipoCaso tc ) {
		this.tc = tc;
	}

	@Override
	public String toString() {
		return "Caso [id_caso=" + id_caso + ", c_folio=" + c_folio + ", id_tc=" + id_tc + ", c_fecha_ini=" + c_fecha_ini + ", c_id_gabinete=" + c_id_gabinete + ", c_status=" + c_status + ", tc=" + tc + ", caso_operacion=" + caso_operacion + "]";
	}

}
