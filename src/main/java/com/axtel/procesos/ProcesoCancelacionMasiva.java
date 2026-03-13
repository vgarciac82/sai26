package com.axtel.procesos;


import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

import com.syc.cfdi.db.CloseObject;
import com.syc.contable.AccountingEngine;
import com.syc.contable.core.RelacionGastosManager;
import com.syc.gestion.util.Util;


public class ProcesoCancelacionMasiva {

	public static void main( String[] args ) {

		System.out.println( "[MAIN] Starting cancelation process..." );

		Connection conn = null;

		AccountingEngine accEng = new AccountingEngine();
		accEng.setValidaInsuficienciaDeSaldo( true );
		System.out.println( "[MAIN] AccountingEngine created. validaInsuficienciaDeSaldo=true" );

		try {

			System.out.println( "[MAIN] Getting standalone DB connection..." );
			conn = Util.getStandAloneConnection();
			System.out.println( "[MAIN] DB connection OK." );

			if ( args == null || args.length == 0 ) {
				System.out.println( "[MAIN][ERROR] Missing argument args[0] (folioMasivo). Exiting." );
				return;
			}

			System.out.println( "[MAIN] Reading folioMasivo from args[0]: " + args[0] );
			int folioMasivo = Integer.parseInt( args[0] );
			System.out.println( "[MAIN] folioMasivo parsed: " + folioMasivo );

			System.out.println( "[MAIN] Querying folios for carga masiva (folioMasivo=" + folioMasivo + ")..." );
			List<Integer> l = RelacionGastosManager.obtenFoliosCargaMasiva( conn, folioMasivo );
			System.out.println( "[MAIN] Folios retrieved: " + ( l == null ? "null" : l.size() ) );

			if ( l == null || l.isEmpty() ) {
				System.out.println( "[MAIN] No folios found for folioMasivo=" + folioMasivo + ". Nothing to cancel." );
				return;
			}

			for ( int i : l ) {
				System.out.println( "[MAIN] ------------------------------------------------------------" );
				System.out.println( "[MAIN] Processing nFolioRelacionGastos=" + i );

				System.out.println( "[MAIN] Getting folioApartado for nFolioRelacionGastos=" + i + "..." );
				int folioApartado = RelacionGastosManager.getFolioApartado( conn, i );
				System.out.println( "[MAIN] folioApartado obtained: " + folioApartado );

				System.out.println( "[MAIN] Canceling accounting application: RELACIONGASTOS, folio=" + i );
				accEng.cancelAccountingApplication( conn, "RELACIONGASTOS", String.valueOf( i ), "tRelacionGastosEncabezado", "tRelacionGastosDetalle", "nFolioRelacionGastos" );
				System.out.println( "[MAIN] RELACIONGASTOS canceled OK for folio=" + i );

				System.out.println( "[MAIN] Canceling accounting application: PAGOAPARTADO, folio=" + folioApartado );
				accEng.cancelAccountingApplication( conn, "PAGOAPARTADO", String.valueOf( folioApartado ), "tPagoApartadoEncabezado", "tPagoApartadoDetalle", "nFolioPagoApartado" );
				System.out.println( "[MAIN] PAGOAPARTADO canceled OK for folio=" + folioApartado );
			}

			System.out.println( "[MAIN] Done. All folios processed." );

			System.out.println( "[MAIN] ¿Quieres dar commit? (Y/N)" );

			Scanner scanner = new Scanner( System.in );
			String option = scanner.nextLine();

			if ( "Y".equalsIgnoreCase( option ) ) {
				System.out.println( "[MAIN] Commit selected. Committing transaction..." );
				conn.commit();
				System.out.println( "[MAIN] Commit executed successfully." );
			} else {
				System.out.println( "[MAIN] Rollback selected. Rolling back transaction..." );
				Util.rollback( conn );
				System.out.println( "[MAIN] Rollback executed." );
			}

		} catch ( Exception e ) {

			System.out.println( "[MAIN][ERROR] Unexpected error: " + e.getClass().getName() + " - " + e.getMessage() );
			e.printStackTrace( System.out );

			System.out.println( "[MAIN] Rolling back transaction..." );
			Util.rollback( conn );
			System.out.println( "[MAIN] Rollback executed." );

		} finally {

			System.out.println( "[MAIN] Closing DB connection..." );
			CloseObject.closeObject( conn );
			System.out.println( "[MAIN] Connection closed. Exiting." );

		}
	}

}
