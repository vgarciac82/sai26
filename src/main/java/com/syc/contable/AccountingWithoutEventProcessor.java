package com.syc.contable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import com.scottlogic.util.SortedList;
import com.syc.contable.AccountingEventProcessor.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class AccountingWithoutEventProcessor implements AccountingEventProcessorInterface {

    public static final String EVENTO_CARGO = "CARGO";

    public static final String EVENTO_ABONO = "ABONO";

    public static final String FIELD_EJERCICIO_FISCAL = "aEjercicioFiscal";

    public static final String FIELD_CENTRO_CONTABLE = "cCentroContable";

    public static final String FIELD_DOC_RENGLON = "nDocRenglon";

    public static final String FIELD_CUENTA = "nCuenta";

    public static final String FIELD_SUBCUENTA = "nSubCuenta";

    public static final String FIELD_COMPONENTE = "mImporte";

    private static final Logger log = LoggerFactory.getLogger(AccountingWithoutEventProcessor.class);

    private Map<String, Account> cuentas;

    public AccountingWithoutEventProcessor() {
        this.cuentas = new Hashtable<String, Account>();
    }

    public List<AccountingMovement> getMovements(Connection conn, String event, Map<String, String> values) throws AccountingEventProcessorException {
        List<AccountingMovement> movements = new SortedList<AccountingMovement>(new AccountingMovementComparator());
        Account cta = buildAccount(conn, values);
        if (cta.isSubCuentaRequerida() && (values.get(FIELD_SUBCUENTA) == null))
            throw new AccountingEventProcessorException(String.format("La subcuenta es requerida para esta cuenta (%s)", values.get(FIELD_SUBCUENTA), cta.getCuenta()));
        if (!cta.isSubCuentaRequerida() && ((values.get(FIELD_SUBCUENTA) != null) && (values.get(FIELD_SUBCUENTA).trim().length() > 0)))
            throw new AccountingEventProcessorException(String.format("La cuenta (%s) no requiere de la subcuenta (%s)", cta.getCuenta(), values.get(FIELD_SUBCUENTA)));
        AccountingMovement ac = new AccountingMovement(cta.getCuenta(), cta.isSubCuentaRequerida() ? values.get(cta.getSubCuenta()).trim() : null, Double.parseDouble(values.get(FIELD_COMPONENTE)), EVENTO_CARGO.equalsIgnoreCase(event), cta.esAcreedora(), cta.verificaSaldo(), cta.getCentroContable(), cta.getEjercicioFiscal(), Integer.parseInt(values.get(FIELD_DOC_RENGLON)), cta.getDescCuenta(), cta.isPresupuestal(), values.get("cUnidadResponsable"));
        ac.setParcial(values.get("parcial"));
        movements.add(ac);
        return movements;
    }

    // METODOS PRIVADOS
    private Account buildAccount(Connection conn, Map<String, String> values) throws AccountingEventProcessorException {
        Account cta = null;
        String cuenta = values.get(FIELD_CUENTA);
        PreparedStatement pstmnt = null;
        String alternativeSubActName = null;
        ResultSet rs = null;
        if (cuenta == null)
            throw new AccountingEventProcessorException("La cuenta no debe ser nula");
        if (cuenta.trim().length() == 0)
            throw new AccountingEventProcessorException(String.format("La cuenta (%s) no debe estar vacia", cuenta));
        try {
            // Valida que la cuenta exista
            if (!cuentas.containsKey(cuenta)) {
                pstmnt = conn.prepareStatement("SELECT nCuenta, cSubcuenta, VerificaSaldo, NaturalezaCuenta, AplicacionCuenta, dCuenta, TipoBalance, TipoCuenta FROM tCuentas WITH (NOLOCK) WHERE nCuenta = ?");
                pstmnt.setString(1, cuenta);
                rs = pstmnt.executeQuery();
                if (!rs.next())
                    throw new AccountingEventProcessorException("No se localiz\u00F3 la cuenta '" + cuenta + "'");
                if ("N".equalsIgnoreCase(rs.getString("AplicacionCuenta")))
                    throw new AccountingEventProcessorException("La cuenta '" + cuenta + "' no es de aplicaci\u00F3n");
                if ((rs.getString("cSubcuenta") != null) && (values.get(rs.getString("cSubcuenta")) == null) && (values.get("nsubcuenta") == null))
                    throw new AccountingEventProcessorException("No se recibi\u00F3 el componente '" + rs.getString("cSubcuenta") + "' de subcuenta para cuenta '" + cuenta + "'");
                else
                    alternativeSubActName = "nsubcuenta";
                String centroContable;
                if ((centroContable = values.get(FIELD_CENTRO_CONTABLE)) == null)
                    throw new AccountingEventProcessorException("No se recibi\u00F3 el centro contable para cuenta '" + cuenta + "'");
                String ejercicoFiscal;
                if ((ejercicoFiscal = values.get(FIELD_EJERCICIO_FISCAL)) == null)
                    throw new AccountingEventProcessorException("No se recibi\u00F3 el ejercicio fiscal");
                cta = new Account(rs.getString("nCuenta"), (alternativeSubActName == null ? rs.getString("cSubcuenta") : alternativeSubActName), "S".equalsIgnoreCase(rs.getString("VerificaSaldo")), "D".equalsIgnoreCase(rs.getString("NaturalezaCuenta")), centroContable, ejercicoFiscal, rs.getString("dCuenta"), rs.getString("TipoBalance"), rs.getString("TipoCuenta"));
                cuentas.put(rs.getString("nCuenta"), cta);
            } else {
                cta = cuentas.get(cuenta);
                if (cta == null)
                    throw new AccountingEventProcessorException("No se localiz\u00F3 la cuenta '" + cuenta + "'");
                String centroContable;
                if ((centroContable = values.get(FIELD_CENTRO_CONTABLE)) == null)
                    throw new AccountingEventProcessorException("No se recibi\u00F3 el centro contable para cuenta '" + cuenta + "'");
                String ejercicoFiscal;
                if ((ejercicoFiscal = values.get(FIELD_EJERCICIO_FISCAL)) == null)
                    throw new AccountingEventProcessorException("No se recibi\u00F3 el ejercicio fiscal");
                if (!centroContable.equals(cta.getCentroContable()))
                    cta.setCentroContable(centroContable);
                if (!ejercicoFiscal.equals(cta.getEjercicioFiscal()))
                    cta.setEjercicioFiscal(ejercicoFiscal);
            }
        } catch (Exception exc) {
            if (exc instanceof AccountingEventProcessorException)
                throw (AccountingEventProcessorException) exc;
            else
                throw new AccountingEventProcessorException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
            try {
                if (pstmnt != null)
                    pstmnt.close();
            } catch (Exception exc) {
                log.warn("Cerrando PreparedStatement", exc);
            }
        }
        return cta;
    }
}
