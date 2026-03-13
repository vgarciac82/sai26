package com.syc.sai.procesos;


import java.sql.Connection;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.axtel.contratos.penalties.businessLogic.PenaltiesImplements;
import com.axtel.contratos.penalties.core.PenaltyAndDeduction;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class EnviaCorreoPECO {

	private StringBuilder					query;
	private Connection						conn;
	ResultSetHandler<PenaltyAndDeduction>	rsHandler;

	public EnviaCorreoPECO( ) throws Exception {
		conn = Util.getStandAloneConnection();

		query = new StringBuilder();
		query.append( "SELECT penaltyDeduction.nidpenaltydeduction      AS nIdPenaltyDeduction, " );
		query.append( "       cfolio                                    AS cFolio, " );
		query.append( "       penaltyDeduction.cidcontratodefinitivo    AS cIdContratoDefinitivo, " );
		query.append( "       nidestate                                 AS nIdEstate, " );
		query.append( "       fcapturedate                              AS fCaptureDate, " );
		query.append( "       ccaptureuser                              AS cCaptureUsser, " );
		query.append( "       lpenalty                                  AS lPenalty, " );
		query.append( "       ldeduction                                AS lDeduction, " );
		query.append( "       lpenalty                                  AS checkPenaConv, " );
		query.append( "       cidtipocontrato                           AS cTipoContrato, " );
		query.append( "       proveedor                                 AS cProveedor, " );
		query.append( "       cdocumenthaplicado                        AS cDocumentHAplicado, " );
		query.append( "       cobservations                             AS cObservations, " );
		query.append( "       cconcept                                  AS cConcepto, " );
		query.append( "       penaltyDeduction.cnumberjob               AS cOficio, " );
		query.append( "       nidperiod                                 AS nPeriodo, " );
		query.append( "       cont.cnocontratocnet                      AS cNumContratoCNET, " );
		query.append( "       Isnull(penalty.mamountpenaltytotal, 0)    mAmountPenaltyTotal, " );
		query.append( "       Isnull(deduction.mamountdeductiontotal, 0)mAmountDeductionTotal, " );
		query.append( "       3                                         AS nIdOper " );
		query.append( "FROM   mpenaltydeduction penaltyDeduction WITH(nolock) " );
		query.append( "       INNER JOIN(SELECT '[ ' + cont.cidrfc + ' ] ' + prov.crazonsocial AS " );
		query.append( "                         proveedor, " );
		query.append( "                         cnocontratocnet, " );
		query.append( "                         cidcontratodefinitivo, " );
		query.append( "                         cidtipocontrato " );
		query.append( "                  FROM   mcontrato AS cont WITH(nolock) " );
		query.append( "                         INNER JOIN mcatalogoproveedor AS prov WITH(nolock) " );
		query.append( "                                 ON prov.cidrfc = cont.cidrfc " );
		query.append( "                  WHERE  nidestado = 4 " );
		query.append( "                  UNION " );
		query.append( "                  SELECT '[ ' + cont.cidrfc + ' ] ' + prov.crazonsocial AS " );
		query.append( "                         proveedor, " );
		query.append( "                         cnocontratocnet, " );
		query.append( "                         cidcontratodefinitivo, " );
		query.append( "                         cidtipocontrato " );
		query.append( "                  FROM   mplurianualidadcontrato AS cont WITH(nolock) " );
		query.append( "                         INNER JOIN mcatalogoproveedor AS prov WITH(nolock) " );
		query.append( "                                 ON prov.cidrfc = cont.cidrfc " );
		query.append( "                  WHERE  nidestado = 4 " );
		query.append( "                  UNION " );
		query.append( "                  SELECT '[ ' + cont.cidrfc + ' ] ' + prov.crazonsocial AS " );
		query.append( "                         proveedor, " );
		query.append( "                         cnoconvenio                                    AS " );
		query.append( "                         cNoContratoCNET, " );
		query.append( "                         cidcontratodefinitivo, " );
		query.append( "                         Substring(cont.cidcontrato, 1, 2) " );
		query.append( "                         cIdTipoContrato " );
		query.append( "                  FROM   mcontratomodificado AS cont WITH(nolock) " );
		query.append( "                         INNER JOIN mcatalogoproveedor AS prov WITH(nolock) " );
		query.append( "                                 ON prov.cidrfc = cont.cidrfc " );
		query.append( "                  WHERE  nestado = 4 " );
		query.append( "                         AND isconvejercicioant = 1 " );
		query.append( "                  UNION " );
		query.append( "                  SELECT '[ ' + cont.cidrfc + ' ] ' + prov.crazonsocial AS " );
		query.append( "                         proveedor, " );
		query.append( "                         cnocontratocnet, " );
		query.append( "                         cidcontratodefinitivo, " );
		query.append( "                         Substring(cont.cidcontratodefinitivo, 1, 2) " );
		query.append( "                         cIdTipoContrato " );
		query.append( "                  FROM   mcontratoremanenteejercicioanterior AS cont WITH(nolock " );
		query.append( "                         ) " );
		query.append( "                         INNER JOIN mcatalogoproveedor AS prov WITH(nolock) " );
		query.append( "                                 ON Replace(prov.cidrfc, '-', '') = cont.cidrfc " );
		query.append( "                  WHERE  nestado = 4)cont " );
		query.append( "               ON cont.cidcontratodefinitivo = " );
		query.append( "                  penaltyDeduction.cidcontratodefinitivo " );
		query.append( "       LEFT JOIN(SELECT nidpenaltydeduction, " );
		query.append( "                        Sum(mamountpenalty)mAmountPenaltyTotal " );
		query.append( "                 FROM   mpenaltyitems WITH(nolock) " );
		query.append( "                 GROUP  BY nidpenaltydeduction)penalty " );
		query.append( "              ON penalty.nidpenaltydeduction = " );
		query.append( "                 penaltyDeduction.nidpenaltydeduction " );
		query.append( "       LEFT JOIN(SELECT nidpenaltydeduction, " );
		query.append( "                        Sum(mamountdeduction)mAmountDeductionTotal " );
		query.append( "                 FROM   mdeductionitems WITH(nolock) " );
		query.append( "                 GROUP  BY nidpenaltydeduction)deduction " );
		query.append( "              ON deduction.nidpenaltydeduction = " );
		query.append( "                 penaltyDeduction.nidpenaltydeduction " );
		query.append( "WHERE  cfolio = ? " );

		rsHandler = new BeanHandler<PenaltyAndDeduction>( PenaltyAndDeduction.class );

	}

	public static void main( String[] args ) {
		if ( args.length == 0 ) {
			System.out.println( "No se recibio la ruta con los folios a procesar." );
			return;
		}

		EnviaCorreoPECO proceso = null;

		try {

			String fileName = args[0];

			proceso = new EnviaCorreoPECO();

			proceso.enviaCorreos( fileName );

		} catch ( Exception e ) {
			System.out.println( "Error: " + e.toString() );
			e.printStackTrace();
		} finally {
			if ( proceso != null )
				proceso.cierraConexion();
		}
	}

	private void enviaCorreos( String fileName ) throws Exception {
		List<String> folios = Util.readFileByLine( fileName );
		QueryRunner run = new QueryRunner();

		for ( String folio : folios ) {

			System.out.println( folio );
			PenaltyAndDeduction penalty = run.query( conn, query.toString(), rsHandler, folio );
			if ( penalty != null ) {
				System.out.println( penalty );
				String tipo = "VALIDADO";
				penalty.setcEmailCaptureUsser( "hfariasr@axtel.com.mx" );
				String subject = "HA SIDO VALIDADO EL PROCESO DE CÁLCULO DE LAS PENAS CONVENCIONALES Y/O DEDUCCIONES DEL OFICIO [" + penalty.getcOficio() + "]";
				StringBuilder bodymail = PenaltiesImplements.bodyEmail( penalty.getcNumContratoCNET(), penalty.getcProveedor(), penalty.getcOficio(), tipo );

				AlarmaManager.procesaAlarmaCNF( conn, "", null, null, subject.toString(), "erika.cardenas@conafor.gob.mx","", "hfariasr@axtel.com.mx;vgarciac@axtel.com.mx", bodymail.toString(), false );
			} else
				System.out.println( "No se creo objeto para: " + folio );

		}
	}

	private void cierraConexion() {

		CloseObject.closeObject( conn );

	}

}
