package com.syc.ejercido.pagado.manual.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.syc.cfdi.utils.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImpresionChequeManager {

    private static final Logger log = LoggerFactory.getLogger(ImpresionChequeManager.class);

    /**
     * Obtiene nombre del beneficiario.
     *
     * @param conn
     * @param ctaBancaria
     * @return Beneficiario para el que se expidio el cheque.
     */
    public static String getBeneficiario(Connection conn, String ctaBancaria) throws Exception {
        String queryBanco = "SELECT DISTINCT strNombreBeneficiario FROM tUECuentasBancarias WITH(NOLOCK) WHERE strClabe=?";
        PreparedStatement psBanco = null;
        ResultSet rs = null;
        String banco;
        try {
            psBanco = conn.prepareStatement(queryBanco);
            psBanco.setString(1, ctaBancaria);
            rs = psBanco.executeQuery();
            if (rs.next())
                banco = rs.getString(1);
            else
                throw new Exception("No se encontro beneficiario para la cuenta " + ctaBancaria);
            return banco;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(psBanco, false);
        }
    }

    /**
     * Obtiene cuenta bancaria.
     *
     * @param conn
     * @param nFolioCheque
     * @return Cuenta bancaria por la que se expidio el cheque.
     */
    public static String getCtaBancaria(Connection conn, int nFolioCheque) throws Exception {
        String queryCtaBancaria = "SELECT cCuentaBancaria FROM tChequeEncabezado WITH(NOLOCK) WHERE nFolioCheque=?";
        PreparedStatement psCtaBancaria = null;
        ResultSet rs = null;
        String ctaBancaria;
        try {
            psCtaBancaria = conn.prepareStatement(queryCtaBancaria);
            psCtaBancaria.setInt(1, nFolioCheque);
            rs = psCtaBancaria.executeQuery();
            if (rs.next())
                ctaBancaria = rs.getString(1);
            else
                throw new Exception("No se encontro cuenta bancaria para el folio " + nFolioCheque);
            return ctaBancaria;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(psCtaBancaria, false);
        }
    }

    /**
     * Obtiene el folio siguiente del encabezado para cheques.
     *
     * @param conn
     * @param ctaBancaria
     * @return folio siguiente .
     */
    public static int getNexFolioCheque(Connection conn) throws Exception {
        String queryNextFolioCheque = "SELECT MAX(nFolioCheque)+1 FROM tChequeEncabezado WITH(NOLOCK)";
        PreparedStatement psNextFolioCheque = null;
        ResultSet rs = null;
        int folio = 1;
        try {
            psNextFolioCheque = conn.prepareStatement(queryNextFolioCheque);
            rs = psNextFolioCheque.executeQuery();
            if (rs.next())
                folio = rs.getInt(1);
            return folio;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(psNextFolioCheque, false);
        }
    }

    public static int getNexNumeroCheque(Connection conn, String banco) throws Exception {
        int chequesig = 1;
        String queryUpdate = "UPDATE  CF_SEQUENCE  WITH (ROWLOCK) SET seq_value=seq_value+1 WHERE seq_name='CHEQUE '+ ? ";
        String querySelect = "SELECT seq_value FROM CF_SEQUENCE WITH( NOLOCK ) WHERE seq_name='CHEQUE ' + ? ";
        String queryInsert = "INSERT INTO CF_SEQUENCE( seq_name, seq_value ) VALUES( ?,?)";
        PreparedStatement psUpdate = null;
        PreparedStatement psSelect = null;
        PreparedStatement psInsert = null;
        ResultSet rs = null;
        try {
            psUpdate = conn.prepareStatement(queryUpdate);
            psUpdate.setString(1, banco);
            int afectados = psUpdate.executeUpdate();
            if (afectados == 0) {
                psInsert = conn.prepareStatement(queryInsert);
                psInsert.setString(1, "CHEQUE " + banco);
                psInsert.setInt(2, 1);
                psInsert.executeUpdate();
            }
            psSelect = conn.prepareStatement(querySelect);
            psSelect.setString(1, banco);
            rs = psSelect.executeQuery();
            if (rs.next())
                chequesig = rs.getInt(1);
            else
                throw new Exception("No se encontro secuencia para el valor [CHEQUE " + banco + "]");
            return chequesig;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(psUpdate, false);
            CloseObject.closeObject(psSelect, false);
            CloseObject.closeObject(psInsert, false);
        }
    }

    /**
     * Obtiene numero de cheque.
     *
     * @param conn
     * @param nFolioCheque
     * @return Numero de cheque que corresponde al folio.
     */
    public static int getNumeroCheque(Connection conn, int nFolioCheque) throws Exception {
        String queryNumCheque = "SELECT nNumCheque FROM tChequeEncabezado WITH( NOLOCK ) WHERE nFolioCheque=?";
        PreparedStatement psNumCheque = null;
        ResultSet rs = null;
        int chequeanterior;
        try {
            psNumCheque = conn.prepareStatement(queryNumCheque);
            psNumCheque.setInt(1, nFolioCheque);
            rs = psNumCheque.executeQuery();
            if (rs.next())
                chequeanterior = rs.getInt(1);
            else
                throw new Exception("No se encontro numero de cheque para el folio " + nFolioCheque);
            return chequeanterior;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(psNumCheque, false);
        }
    }

    public static String getConcepto(Connection conn, int nNumCheque, String ctaBancaria) throws Exception {
        String queryConcepto = "Select Distinct cConcepto from tChequeEncabezado WITH(NOLOCK) where nNumCheque =? and cCuentaBancaria=? ";
        PreparedStatement psConcepto = null;
        ResultSet rs = null;
        String concepto;
        try {
            psConcepto = conn.prepareStatement(queryConcepto);
            psConcepto.setInt(1, nNumCheque);
            psConcepto.setString(2, ctaBancaria);
            rs = psConcepto.executeQuery();
            if (rs.next())
                concepto = rs.getString(1);
            else
                throw new Exception("No se encontro beneficiario para la cuenta " + nNumCheque);
            return concepto;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(psConcepto, false);
        }
    }

    public static int reemplazaCheque(Connection conn, int nFolioCheque) throws Exception {
        String ctaBancaria;
        String banco;
        String concepto;
        int chequeanterior;
        int folioSigte;
        int chequeSigte;
        int insertadosEnc = 0;
        int insertadosDet = 0;
        String queryInsertEncabezado = // Folio de encabezado
        " INSERT INTO tChequeEncabezado (" + "	aEjercicioFiscal ," + "	cCentroContable ," + "	cUnidadResponsable ," + "	cRamo ," + "	cCuentaBancaria ," + "	nFolioCheque ," + "	nNumCheque ," + "	mImporteCheque ," + "	cIdRFC ," + "	caNoContrarrecibo ," + "	fAplicacion ," + "	fElaboracion ," + "	fEntrega ," + "	cConcepto ," + "	U_LOGIN_captura ," + "	U_LOGIN_revisa ," + "	U_LOGIN_autoriza ," + "	U_LOGIN_imprime ," + "	cDocumentoHaplicado ," + "	nFolioPoliza ," + "	cTipoPoliza ," + "	cUnidadResponsableContable ," + "	nFolioPolizaCancelacion ," + "	fCancelacion ," + "	cTransferencia ," + "	cBeneficiarioAlternativo," + "	nFoliopolizapago," + "	cDescripcionPoliza" + ")" + "SELECT aEjercicioFiscal " + "       ,cCentroContable " + "       ,cUnidadResponsable " + "       ,cRamo" + "       ,cCuentaBancaria " + // Numero de cheque
        "       ,? " + "       ,? " + "       ,mImporteCheque" + "       ,cIdRFC" + "       ,caNoContrarrecibo" + "       , GETDATE()" + "       ,CAST(GETDATE() AS SMALLDATETIME)" + //NvoConcepto
        "       ,CAST(GETDATE() AS SMALLDATETIME)" + "       ,? " + "       ,U_LOGIN_captura" + "       ,U_LOGIN_revisa" + "       ,U_LOGIN_autoriza" + "       ,U_LOGIN_imprime" + "       ,NULL" + "       ,NULL" + "       ,'EG'" + "       ,cUnidadResponsableContable" + "       ,null" + // "       ,'Reemplazo del Cheque ' +CAST(? AS NVARCHAR(20))+' de la cuenta ' + ?" + // Cheque Anterior Banco
        "       ,NULL" + "       ,NULL " + "       ,cBeneficiarioAlternativo " + //NvoConcepto
        "       ,nfoliopolizapago " + "       ,? " + //Cheque anterior
        "FROM   tChequeEncabezado WITH(NOLOCK) " + "WHERE  nFolioCheque = ?  " + "GROUP BY aEjercicioFiscal," + "         cCentroContable," + "         cUnidadResponsable," + "         cRamo," + "         cCuentaBancaria," + "         mImporteCheque," + "         cIdRFC," + "         caNoContrarrecibo," + "         cConcepto, " + "         U_LOGIN_captura, " + "         U_LOGIN_revisa " + "         ,U_LOGIN_autoriza, " + "         U_LOGIN_imprime " + "         ,nFolioPoliza, " + "         cUnidadResponsableContable, " + "	     cBeneficiarioAlternativo" + ",nfoliopolizapago";
        log.debug("Object: {}", "Consulta reemplazo de cheque ENCABEZADO: [" + queryInsertEncabezado + "]");
        String queryInsertDetalle = //numero de cheque
        "INSERT INTO tChequeDetalle " + "SELECT cEjercicio " + "       ,cCentroContable " + "       ,cUnidadResponsable " + "       ,CTAB " + "       ,? " + "       ,nDocRenglon " + "       ,RFC " + "       ,cEvento " + "       ,mImporte " + "       ,mImporteNegativo " + // Folio Cheque Anterior
        "FROM   tChequeDetalle WITH(NOLOCK) " + "WHERE  nFolioCheque=?";
        log.debug("Object: {}", "Consulta reemplazo de cheque DETALLE: [" + queryInsertDetalle + "]");
        String queryUpdateEnc = "UPDATE tChequeEncabezado SET cDocumentoHaplicado='C',fCancelacion= CAST(GETDATE() AS SMALLDATETIME),cDescripcionPoliza='Reemplazo a nuevo numero de Cheque ' +CAST(? AS NVARCHAR(20))+' de la cuenta ' + ? WHERE nFolioCheque=?";
        log.debug("Object: {}", "Consulta reemplazo de cheque Actualiza Cheque : [" + queryUpdateEnc + "]");
        PreparedStatement psInsertEnc = null;
        PreparedStatement psInsertDet = null;
        PreparedStatement psUpdateDet = null;
        try {
            chequeanterior = ImpresionChequeManager.getNumeroCheque(conn, nFolioCheque);
            ctaBancaria = ImpresionChequeManager.getCtaBancaria(conn, nFolioCheque);
            banco = ImpresionChequeManager.getBeneficiario(conn, ctaBancaria);
            folioSigte = ImpresionChequeManager.getNexFolioCheque(conn);
            chequeSigte = ImpresionChequeManager.getNexNumeroCheque(conn, banco);
            concepto = ImpresionChequeManager.getConcepto(conn, chequeanterior, ctaBancaria);
            //Cambiar el Concepto con nuevo numero de cheque
            concepto = concepto.replaceAll("Generación del cheque " + chequeanterior, "Generación del cheque " + chequeSigte);
            log.debug("Object: {}", "Concepto Cheque: " + concepto);
            psInsertEnc = conn.prepareStatement(queryInsertEncabezado);
            psInsertEnc.setString(1, String.valueOf(folioSigte));
            psInsertEnc.setString(2, String.valueOf(chequeSigte));
            psInsertEnc.setString(3, String.valueOf(concepto));
            psInsertEnc.setString(4, String.valueOf(concepto));
            psInsertEnc.setInt(5, nFolioCheque);
            log.debug("Object: {}", "Ejecutando insercion de cheque encabezado: " + psInsertEnc);
            insertadosEnc += psInsertEnc.executeUpdate();
            if (insertadosEnc == 0)
                throw new Exception("No se inserto ningun registro. Verifique que el folio [" + nFolioCheque + "] exista.");
            psInsertDet = conn.prepareStatement(queryInsertDetalle);
            psInsertDet.setString(1, String.valueOf(folioSigte));
            psInsertDet.setInt(2, nFolioCheque);
            log.debug("Object: {}", "Ejecutando insercion de cheque detalle: " + insertadosDet);
            insertadosDet += psInsertDet.executeUpdate();
            if (insertadosDet == 0)
                throw new Exception("No se inserto ningun registro de detalle. Verifique que el folio [" + nFolioCheque + "] contenga detalle.");
            psUpdateDet = conn.prepareStatement(queryUpdateEnc);
            psUpdateDet.setString(1, String.valueOf(chequeSigte));
            psUpdateDet.setString(2, String.valueOf(banco));
            psUpdateDet.setInt(3, nFolioCheque);
            log.debug("Object: {}", "Actualziando estatus de cheque: " + psUpdateDet);
            psUpdateDet.executeUpdate();
            return folioSigte;
        } finally {
            CloseObject.closeObject(psInsertEnc, false);
            CloseObject.closeObject(psInsertDet, false);
            CloseObject.closeObject(psUpdateDet, false);
        }
    }
}
