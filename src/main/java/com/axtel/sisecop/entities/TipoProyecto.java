package com.axtel.sisecop.entities;


public enum TipoProyecto {

	CONSULTORIA( 1, "Consultoría" ), 
	ASESORIA( 2, "Asesoría" ), 
	ESTUDIO( 3, "Estudio" ), 
	INVESTIGACION( 4, "Investigación" ), 
	SIN_REGISTRAR( 5, "Sin Registrar" );

	private final int		id;
	private final String	nombre;

	TipoProyecto( int id, String nombre ) {
		this.id = id;
		this.nombre = nombre;
	}

	public int getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public static TipoProyecto getById( int id ) {
		for ( TipoProyecto tipo : values() ) {
			if ( tipo.getId() == id ) {
				return tipo;
			}
		}
		throw new IllegalArgumentException( "Tipo de proyecto no encontrado para ID: " + id );
	}

	@Override
	public String toString() {
		return this.nombre;
	}
}
