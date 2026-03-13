package com.syc.sai.procesosAutomaticos;


import com.axtel.egresos.viaticos.ViaticosBusinessLogic;


public class EnviaCorreosComisionesPendientes {

	public static void main( String[] args ) {

		ViaticosBusinessLogic vbl = new ViaticosBusinessLogic();
		try {
			vbl.envioCorreosTramitesRetraso( true );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		System.exit( 0 );
	}

}
