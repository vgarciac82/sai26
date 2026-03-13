package com.syc.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.subejercicio.Subejercicio;
import com.syc.subejercicio.UsuarioUEManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableroAlertaSubejercicioBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(TableroAlertaSubejercicioBusinessLogic.class);

    public TableroAlertaSubejercicioBusinessLogic() {
        super();
        super.init();
    }

    public TableroAlertaSubejercicioBusinessLogic(String jniName) {
        super();
        super.init(jniName);
    }

    public List<Map<String, String>> getDatos() throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            List<Map<String, String>> tablero = null;
            tablero = TableroAlertaSubejercicioManager.obtenDatos(conn);
            return tablero;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public double sumaRenglonModificado(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_Modificado_servicios_personales") == null ? "0" : renglon.get("saldo_Modificado_servicios_personales")) + Double.parseDouble(renglon.get("saldo_Modificado_Gastos_de_Operacion") == null ? "0" : renglon.get("saldo_Modificado_Gastos_de_Operacion")) + Double.parseDouble(renglon.get("saldo_Modificado_Subsidios_Corrientes") == null ? "0" : renglon.get("saldo_Modificado_Subsidios_Corrientes")) + Double.parseDouble(renglon.get("saldo_Modificado_Otros_Corrientes") == null ? "0" : renglon.get("saldo_Modificado_Otros_Corrientes")) + Double.parseDouble(renglon.get("saldo_Modificado_Bienes_Muebles_e_Inmuebles") == null ? "0" : renglon.get("saldo_Modificado_Bienes_Muebles_e_Inmuebles")) + Double.parseDouble(renglon.get("saldo_Modificado_Obra_Publica") == null ? "0" : renglon.get("saldo_Modificado_Obra_Publica")) + Double.parseDouble(renglon.get("saldo_Modificado_Otros_de_Inversion_Fisica") == null ? "0" : renglon.get("saldo_Modificado_Otros_de_Inversion_Fisica")) + Double.parseDouble(renglon.get("saldo_Modificado_Subsidios_Inversion") == null ? "0" : renglon.get("saldo_Modificado_Subsidios_Inversion"));
    }

    public double sumaRenglonDisponible(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_disponible_servicios_personales") == null ? "0" : renglon.get("saldo_disponible_servicios_personales")) + Double.parseDouble(renglon.get("saldo_disponible_Gastos_de_Operacion") == null ? "0" : renglon.get("saldo_disponible_Gastos_de_Operacion")) + Double.parseDouble(renglon.get("saldo_disponible_Subsidios_Corrientes") == null ? "0" : renglon.get("saldo_disponible_Subsidios_Corrientes")) + Double.parseDouble(renglon.get("saldo_disponible_Otros_Corrientes") == null ? "0" : renglon.get("saldo_disponible_Otros_Corrientes")) + Double.parseDouble(renglon.get("saldo_disponible_Bienes_Muebles_e_Inmuebles") == null ? "0" : renglon.get("saldo_disponible_Bienes_Muebles_e_Inmuebles")) + Double.parseDouble(renglon.get("saldo_disponible_Obra_Publica") == null ? "0" : renglon.get("saldo_disponible_Obra_Publica")) + Double.parseDouble(renglon.get("saldo_disponible_Otros_de_Inversion_Fisica") == null ? "0" : renglon.get("saldo_disponible_Otros_de_Inversion_Fisica")) + Double.parseDouble(renglon.get("saldo_disponible_Subsidios_Inversion") == null ? "0" : renglon.get("saldo_disponible_Subsidios_Inversion"));
    }

    public double sumaParcialRenglonDisponibleCorriente(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_disponible_servicios_personales") == null ? "0" : renglon.get("saldo_disponible_servicios_personales")) + Double.parseDouble(renglon.get("saldo_disponible_Gastos_de_Operacion") == null ? "0" : renglon.get("saldo_disponible_Gastos_de_Operacion")) + Double.parseDouble(renglon.get("saldo_disponible_Subsidios_Corrientes") == null ? "0" : renglon.get("saldo_disponible_Subsidios_Corrientes")) + Double.parseDouble(renglon.get("saldo_disponible_Otros_Corrientes") == null ? "0" : renglon.get("saldo_disponible_Otros_Corrientes"));
    }

    public double sumaParcialRenglonDisponibleInversion(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_disponible_Bienes_Muebles_e_Inmuebles") == null ? "0" : renglon.get("saldo_disponible_Bienes_Muebles_e_Inmuebles")) + Double.parseDouble(renglon.get("saldo_disponible_Obra_Publica") == null ? "0" : renglon.get("saldo_disponible_Obra_Publica")) + Double.parseDouble(renglon.get("saldo_disponible_Otros_de_Inversion_Fisica") == null ? "0" : renglon.get("saldo_disponible_Otros_de_Inversion_Fisica")) + Double.parseDouble(renglon.get("saldo_disponible_Subsidios_Inversion") == null ? "0" : renglon.get("saldo_disponible_Subsidios_Inversion"));
    }

    public double sumaParcialRenglonModificadoCorriente(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_Modificado_servicios_personales") == null ? "0" : renglon.get("saldo_Modificado_servicios_personales")) + Double.parseDouble(renglon.get("saldo_Modificado_Gastos_de_Operacion") == null ? "0" : renglon.get("saldo_Modificado_Gastos_de_Operacion")) + Double.parseDouble(renglon.get("saldo_Modificado_Subsidios_Corrientes") == null ? "0" : renglon.get("saldo_Modificado_Subsidios_Corrientes")) + Double.parseDouble(renglon.get("saldo_Modificado_Otros_Corrientes") == null ? "0" : renglon.get("saldo_Modificado_Otros_Corrientes"));
    }

    public double sumaParcialRenglonModificadoInversion(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_Modificado_Bienes_Muebles_e_Inmuebles") == null ? "0" : renglon.get("saldo_Modificado_Bienes_Muebles_e_Inmuebles")) + Double.parseDouble(renglon.get("saldo_Modificado_Obra_Publica") == null ? "0" : renglon.get("saldo_Modificado_Obra_Publica")) + Double.parseDouble(renglon.get("saldo_Modificado_Otros_de_Inversion_Fisica") == null ? "0" : renglon.get("saldo_Modificado_Otros_de_Inversion_Fisica")) + Double.parseDouble(renglon.get("saldo_Modificado_Subsidios_Inversion") == null ? "0" : renglon.get("saldo_Modificado_Subsidios_Inversion"));
    }

    //modificado individual corriente
    public double saldoRenglonModificadoServiciosPersonales(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_Modificado_servicios_personales") == null ? "0" : renglon.get("saldo_Modificado_servicios_personales"));
    }

    public double saldoRenglonModificadoGastosOperacion(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_Modificado_Gastos_de_Operacion") == null ? "0" : renglon.get("saldo_Modificado_Gastos_de_Operacion"));
    }

    public double saldoRenglonModificadoSubsidiosCorrientes(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_Modificado_Subsidios_Corrientes") == null ? "0" : renglon.get("saldo_Modificado_Subsidios_Corrientes"));
    }

    public double saldoRenglonModificadoOtrosCorrientes(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_Modificado_Otros_Corrientes") == null ? "0" : renglon.get("saldo_Modificado_Otros_Corrientes"));
    }

    //disponibles individual corriente
    public double saldoRenglonDisponibleServiciosPersonales(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_disponible_servicios_personales") == null ? "0" : renglon.get("saldo_disponible_servicios_personales"));
    }

    public double saldoRenglonDisponibleGastosOperacion(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_disponible_Gastos_de_Operacion") == null ? "0" : renglon.get("saldo_disponible_Gastos_de_Operacion"));
    }

    public double saldoRenglonDisponibleSubsidiosCorrientes(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_disponible_Subsidios_Corrientes") == null ? "0" : renglon.get("saldo_disponible_Subsidios_Corrientes"));
    }

    public double saldoRenglonDisponibleOtrosCorrientes(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_disponible_Otros_Corrientes") == null ? "0" : renglon.get("saldo_disponible_Otros_Corrientes"));
    }

    //Modificado individual inversion
    public double saldoRenglonModificadoMueblesInmuebles(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_Modificado_Bienes_Muebles_e_Inmuebles") == null ? "0" : renglon.get("saldo_Modificado_Bienes_Muebles_e_Inmuebles"));
    }

    public double saldoRenglonModificadoObraPublica(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_Modificado_Obra_Publica") == null ? "0" : renglon.get("saldo_Modificado_Obra_Publica"));
    }

    public double saldoRenglonModificadoInversionFisica(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_Modificado_Otros_de_Inversion_Fisica") == null ? "0" : renglon.get("saldo_Modificado_Otros_de_Inversion_Fisica"));
    }

    public double saldoRenglonModificadoSubsidiosInversion(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_Modificado_Subsidios_Inversion") == null ? "0" : renglon.get("saldo_Modificado_Subsidios_Inversion"));
    }

    //disponibles individual inversion
    public double saldoRenglonDisponibleMueblesInmuebles(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_disponible_Bienes_Muebles_e_Inmuebles") == null ? "0" : renglon.get("saldo_disponible_Bienes_Muebles_e_Inmuebles"));
    }

    public double saldoRenglonDisponibleObraPublica(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_disponible_Obra_Publica") == null ? "0" : renglon.get("saldo_disponible_Obra_Publica"));
    }

    public double saldoRenglonDisponibleInversionFisica(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_disponible_Otros_de_Inversion_Fisica") == null ? "0" : renglon.get("saldo_disponible_Otros_de_Inversion_Fisica"));
    }

    public double saldoRenglonDisponibleSubsidiosInversion(Map<String, String> renglon) {
        return Double.parseDouble(renglon.get("saldo_disponible_Subsidios_Inversion") == null ? "0" : renglon.get("saldo_disponible_Subsidios_Inversion"));
    }

    //porcentajes individual corriente
    public String porcentajeRenglonServiciosPersonales(Map<String, String> renglon) {
        return renglon.get("porcentaje_subejercicio_servicios_personales") == null ? "0" : renglon.get("porcentaje_subejercicio_servicios_personales");
    }

    public String porcentajeRenglonGastosOperacion(Map<String, String> renglon) {
        return renglon.get("porcentaje_subejercicio_Gastos_de_Operacion") == null ? "0" : renglon.get("porcentaje_subejercicio_Gastos_de_Operacion");
    }

    public String porcentajeRenglonSubsidiosCorrientes(Map<String, String> renglon) {
        return renglon.get("porcentaje_subejercicio_Subsidios_Corrientes") == null ? "0" : renglon.get("porcentaje_subejercicio_Subsidios_Corrientes");
    }

    public String porcentajeRenglonOtrosCorrientes(Map<String, String> renglon) {
        return renglon.get("porcentaje_subejercicio_Otros_Corrientes") == null ? "0" : renglon.get("porcentaje_subejercicio_Otros_Corrientes");
    }

    //porcentajes individual inversion
    public String porcentajeRenglonMueblesInmuebles(Map<String, String> renglon) {
        return renglon.get("porcentaje_subejercicio_Bienes_Muebles_e_Inmuebles") == null ? "0" : renglon.get("porcentaje_subejercicio_Bienes_Muebles_e_Inmuebles");
    }

    public String porcentajeRenglonObraPublica(Map<String, String> renglon) {
        return renglon.get("porcentaje_subejercicio_Obra_Publica") == null ? "0" : renglon.get("porcentaje_subejercicio_Obra_Publica");
    }

    public String porcentajeRenglonInversionFisica(Map<String, String> renglon) {
        return renglon.get("porcentaje_subejercicio_Otros_de_Inversion_Fisica") == null ? "0" : renglon.get("porcentaje_subejercicio_Otros_de_Inversion_Fisica");
    }

    public String porcentajeRenglonSubsidiosInversion(Map<String, String> renglon) {
        return renglon.get("porcentaje_subejercicio_Subsidios_Inversion") == null ? "0" : renglon.get("porcentaje_subejercicio_Subsidios_Inversion");
    }

    public String porcentaje_subejercicio_Total(Map<String, String> renglon) {
        return renglon.get("porcentaje_subejercicio_Total") == null ? "0" : renglon.get("porcentaje_subejercicio_Total");
    }

    public static Map<String, Subejercicio> generaDestinatarios(Connection conn, double porcentaje) throws Exception {
        String querySubejercicio = " select * from tPorcentajeEjercidoDisponible with(nolock) where pctgEjercido > ? order by cUnidadEjecutora ";
        Map<String, Subejercicio> correos = new HashMap<String, Subejercicio>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(querySubejercicio);
            ps.setDouble(1, porcentaje);
            rs = ps.executeQuery();
            while (rs.next()) {
                String ue = rs.getString("cUnidadEjecutora");
                Subejercicio sub = correos.get(ue);
                if (sub == null) {
                    sub = new Subejercicio();
                    sub.setUsuarios(UsuarioUEManager.buscaUsuariosUE(conn, ue, "REVISORES_ADECUACIONES"));
                    correos.put(ue, sub);
                }
                sub.getClaves().add(" EP = " + rs.getString("EP") + " Modificado = " + String.valueOf(rs.getDouble("saldoModificado")) + " Disponible = " + rs.getString("saldoEjercido"));
            }
            return correos;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }
}
