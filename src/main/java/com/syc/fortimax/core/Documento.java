package com.syc.fortimax.core;


import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;


public class Documento {

	private String		autor;
	private int			clase_documento;
	private String		compartir;
	private String		descripcion;
	private String		estado_documento;
	private int			esVersion;
	private String		extension;
	private Date		fh_creacion;
	private Date		fh_modificacion;
	private Timestamp	fh_vigencia;
	private int			id_carpeta_padre;
	private int			id_documento;
	private int			id_gabinete;
	private int			id_tipo_docto;
	private String		materia;
	private String		nombre_documento;
	private String		nombre_tipo_docto;
	private String		nombre_usuario;
	private int			numero_accesos;
	private int			numero_paginas;
	private Pagina[]	paginas;
	private int			prioridad;
	private double		tamano_bytes;
	private String		titulo;
	private String		titulo_aplicacion;
	private String		token_compartir;

	public Documento( ) {
		id_gabinete = -1;
		id_carpeta_padre = -1;
		id_documento = -1;
		prioridad = 3;
		id_tipo_docto = 1;
		fh_creacion = new Date( System.currentTimeMillis() );
		fh_modificacion = new Date( System.currentTimeMillis() );
		numero_accesos = 0;
		numero_paginas = 0;
		clase_documento = 0;
		estado_documento = new String( "V" );
		tamano_bytes = Double.parseDouble( "0" );
		compartir = new String( "N" );
		token_compartir = new String();
		extension = new String();
		paginas = new Pagina [0];
		esVersion = 0;
		materia = "ORIGINAL";
	}

	public String getAutor() {
		return autor;
	}

	public int getClaseDocumento() {
		return clase_documento;
	}

	public String getCompartir() {
		return compartir;
	}

	public String getDescripcion() {
		return ( descripcion == null ? "" : descripcion );
	}

	public String getEstadoDocumento() {
		return estado_documento;
	}

	public int getEsVersion() {
		return esVersion;
	}

	public String getExtension() {
		return extension;
	}

	public Date getFechaCreacion() {
		return fh_creacion;
	}

	public Date getFechaModificacion() {
		return fh_modificacion;
	}

	public Timestamp getFh_vigencia() {
		return fh_vigencia;
	}

	public String[] getFilesNames() {
		String filesNames[] = new String [paginas.length];

		for ( int i = 0; i < paginas.length; i++ ) {
			filesNames[i] = nombre_documento + ( ( id_tipo_docto == 2 ) ? "_" + ( i + 1 ) : "" ) + paginas[i].getPageExtension();
		}

		return filesNames;
	}

	public String[] getFullPathFilesNames() {
		String fullPathFilesNames[] = new String [paginas.length];

		for ( int i = 0; i < paginas.length; i++ ) {
			fullPathFilesNames[i] = paginas[i].getUnidadDisco() + paginas[i].getRutaBase() + paginas[i].getRutaDirectorio() + paginas[i].getNomArchivoVol();
		}

		return fullPathFilesNames;
	}

	public int getIdCarpetaPadre() {
		return id_carpeta_padre;
	}

	public int getIdDocumento() {
		return id_documento;
	}

	public int getIdGabinete() {
		return id_gabinete;
	}

	public int getIdTipoDocto() {
		return id_tipo_docto;
	}

	public String getMateria() {
		return materia;
	}

	public String getNombreDocumento() {
		return nombre_documento;
	}

	public String getNombreTipoDocto() {
		return nombre_tipo_docto;
	}

	public String getNombreUsuario() {
		return nombre_usuario;
	}

	public int getNumeroAccesos() {
		return numero_accesos;
	}

	public int getNumeroPaginas() {
		return numero_paginas;
	}

	public Pagina getPaginaDocumento( int index ) {
		return paginas[index];
	}

	public Pagina[] getPaginasDocumento() {
		return paginas;
	}

	public int getPrioridad() {
		return prioridad;
	}

	public double getTamanoBytes() {
		return tamano_bytes;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getTituloAplicacion() {
		return titulo_aplicacion;
	}

	public String getTokenCompartir() {
		return token_compartir;
	}

	public boolean isCompartido() {
		return "S".equals( compartir );
	}

	public void setAutor( String autor ) {
		this.autor = autor;
	}

	public void setClaseDocumento( int clase_documento ) {
		this.clase_documento = clase_documento;
	}

	public void setCompartir( String compartir ) {
		this.compartir = compartir;
	}

	public void setDescripcion( String descripcion ) {
		this.descripcion = descripcion;
	}

	public void setEstadoDocumento( String estado_documento ) {
		this.estado_documento = estado_documento;
	}

	public void setEsVersion( int esVersion ) {
		this.esVersion = esVersion;
	}

	public void setExtension( String extension ) {
		this.extension = ( extension != null ) ? extension : "";
	}

	public void setFechaCreacion( Date fh_creacion ) {
		this.fh_creacion = fh_creacion;
	}

	public void setFechaModificacion( Date fh_modificacion ) {
		this.fh_modificacion = fh_modificacion;
	}

	public void setFh_vigencia( Timestamp fhVigencia ) {
		fh_vigencia = fhVigencia;
	}

	public void setIdCarpetaPadre( int id_carpeta_padre ) {
		this.id_carpeta_padre = id_carpeta_padre;
	}

	public void setIdDocumento( int id_documento ) {
		this.id_documento = id_documento;
	}

	public void setIdGabinete( int id_gabinete ) {
		this.id_gabinete = id_gabinete;
	}

	public void setIdTipoDocto( int id_tipo_docto ) {
		this.id_tipo_docto = id_tipo_docto;
	}

	public void setMateria( String materia ) {
		this.materia = materia;
	}

	public void setNombreDocumento( String nombre_documento ) {
		this.nombre_documento = nombre_documento;
	}

	public void setNombreTipoDocto( String nombre_tipo_docto ) {
		this.nombre_tipo_docto = nombre_tipo_docto;
	}

	public void setNombreUsuario( String nombre_usuario ) {
		this.nombre_usuario = nombre_usuario;
	}

	public void setNumeroAccesos( int numero_accesos ) {
		this.numero_accesos = numero_accesos;
	}

	public void setNumeroPaginas( int numero_paginas ) {
		this.numero_paginas = ( numero_paginas < 0 ? 0 : numero_paginas );
	}

	public void setPaginasDocumento( Pagina[] paginas ) {
		this.paginas = paginas;
	}

	public void setPrioridad( int prioridad ) {
		this.prioridad = prioridad;
	}

	public void setTamanoBytes( double tamano_bytes ) {
		this.tamano_bytes = tamano_bytes;
	}

	public void setTitulo( String titulo ) {
		this.titulo = titulo;
	}

	public void setTituloAplicacion( String titulo_aplicacion ) {
		this.titulo_aplicacion = titulo_aplicacion;
	}

	public void setTokenCompartir( String token_compartir ) {
		this.token_compartir = token_compartir;
	}

	public String toFortimax() {
		return getTituloAplicacion() + "_" + "G" + getIdGabinete() + "C" + getIdCarpetaPadre() + "D" + getIdDocumento();
	}

	@Override
	public String toString() {
		return "Documento [autor=" + autor + ", clase_documento=" + clase_documento + ", compartir=" + compartir + ", descripcion=" + descripcion + ", estado_documento=" + estado_documento + ", esVersion=" + esVersion + ", extension=" + extension + ", fh_creacion=" + fh_creacion + ", fh_modificacion=" + fh_modificacion + ", fh_vigencia=" + fh_vigencia + ", id_carpeta_padre=" + id_carpeta_padre + ", id_documento=" + id_documento + ", id_gabinete=" + id_gabinete + ", id_tipo_docto=" + id_tipo_docto + ", materia=" + materia + ", nombre_documento=" + nombre_documento + ", nombre_tipo_docto=" + nombre_tipo_docto + ", nombre_usuario=" + nombre_usuario + ", numero_accesos=" + numero_accesos + ", numero_paginas=" + numero_paginas + ", paginas=" + Arrays.toString( paginas ) + ", prioridad=" + prioridad + ", tamano_bytes=" + tamano_bytes + ", titulo=" + titulo + ", titulo_aplicacion=" + titulo_aplicacion + ", token_compartir=" + token_compartir + "]";
	}
 

}
