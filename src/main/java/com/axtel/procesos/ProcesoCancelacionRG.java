	package com.axtel.procesos;


	import java.sql.Connection;
	import java.util.Scanner;

	import com.syc.cfdi.db.CloseObject;
	import com.syc.contable.AccountingEngine;
	import com.syc.contable.core.RelacionGastosManager;
	import com.syc.gestion.util.Util;


	public class ProcesoCancelacionRG  {

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
					System.out.println( "[MAIN][ERROR] Missing argument args[0] (CxP). Exiting." );
					return;
				}

				for ( String cx : args ) {
					System.out.println( "[MAIN] ------------------------------------------------------------" );
					System.out.println( "[MAIN] Reading folio from [" + cx + "]: " );
					int folioRG = RelacionGastosManager.obtenFolioContrarrecibo( conn, cx );
					System.out.println( "[MAIN] folio RG parsed: " + folioRG );

					
					System.out.println( "[MAIN] Processing nFolioRelacionGastos=" + folioRG );

					System.out.println( "[MAIN] Getting folioApartado for nFolioRelacionGastos=" + folioRG + "..." );
					int folioApartado = RelacionGastosManager.getFolioApartado( conn, folioRG );
					System.out.println( "[MAIN] folioApartado obtained: " + folioApartado );

					System.out.println( "[MAIN] Canceling accounting application: RELACIONGASTOS, folio=" + folioRG );
					accEng.cancelAccountingApplication( conn, "RELACIONGASTOS", String.valueOf( folioRG ), "tRelacionGastosEncabezado", "tRelacionGastosDetalle", "nFolioRelacionGastos" );
					System.out.println( "[MAIN] RELACIONGASTOS canceled OK for folio=" + folioRG );

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
				scanner.close();

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
