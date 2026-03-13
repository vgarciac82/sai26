package com.axtel.sisecop.entities;


public enum TipoExcepcion {

	AMPLIACION( 1, "AMPLIACION", "etapa" ), COMPLEMENTO( 3, "COMPLEMENTO", "complemento" ), CONTINUACION( 4, "CONTINUACION", "continuación" ), MODIFICACION( 2,
			"MODIFICACION", "modificación" );

	public static TipoExcepcion findById( int id ) {
		for ( TipoExcepcion excepcion : values() ) {
			if ( excepcion.getId() == id ) {
				return excepcion;
			}
		}
		throw new IllegalArgumentException( "No enum found with id: " + id );
	}

	public static TipoExcepcion findByTipo( String tipo ) {
		for ( TipoExcepcion excepcion : values() ) {
			if ( excepcion.getTipo().equalsIgnoreCase( tipo ) ) {
				return excepcion;
			}
		}
		throw new IllegalArgumentException( "No enum found with tipo: " + tipo );
	}

	public static TipoExcepcion findByVerbo( String verbo ) {
		for ( TipoExcepcion excepcion : values() ) {
			if ( excepcion.getVerbo().equalsIgnoreCase( verbo ) ) {
				return excepcion;
			}
		}
		throw new IllegalArgumentException( "No enum found with verbo: " + verbo );
	}

	private final int		id;

	private final String	tipo;

	private final String	verbo;

	TipoExcepcion( int id, String tipo, String verbo ) {
		this.id = id;
		this.tipo = tipo;
		this.verbo = verbo;
	}

	public int getId() {
		return id;
	}

	public String getTipo() {
		return tipo;
	}

	public String getVerbo() {
		return verbo;
	}

	@Override
	public String toString() {
		return String.format( "%s(%d, %s)", name(), id, verbo );
	}
}
