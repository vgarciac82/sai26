package com.syc.adquisiciones.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jfree.util.Log;
import com.syc.adquisiciones.core.DatosProcedimiento;
import com.syc.adquisiciones.servlet.ProcedimientoServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcedimientoManager {

    private static Logger log = LoggerFactory.getLogger(ProcedimientoManager.class);

    public boolean borraFechas(Connection conn, String cIdProcedimiento) throws SQLException {
        String query = "";
        PreparedStatement pstm = null;
        boolean resp = false;
        try {
            query = "DELETE dbo.mProcedimientoFechas WHERE nIdProcedimiento='" + cIdProcedimiento + "'";
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            pstm.executeUpdate();
            resp = true;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
        return resp;
    }

    public boolean guardaFechas(Connection conn, String cIdProcedimiento, ArrayList<List<String>> fechas, String cidContDef) throws SQLException {
        String query = "";
        PreparedStatement pstm = null;
        boolean resp = false;
        List<String> fila = null;
        Iterator<List<String>> itr = null;
        try {
            query = "";
            itr = fechas.iterator();
            fila = new ArrayList<String>();
            while (itr.hasNext()) {
                resp = false;
                fila = itr.next();
                query = "INSERT INTO dbo.mProcedimientoFechas( nIdFecha, nIdProcedimiento, fecha )VALUES  ( " + fila.get(0) + ",'" + cIdProcedimiento + "',CONVERT(DATE,'" + fila.get(1) + "'))";
                log.info("Object: {}", query.toString());
                pstm = conn.prepareStatement(query);
                pstm.executeUpdate();
                if (Integer.parseInt(fila.get(0)) == 12) {
                    //actualizar fecha de formalización en mcontrato y pcontratodiverso
                    updateFechaFormalizacionContrato(conn, cidContDef, fila.get(1));
                    updateFechaFormalizacionpContratoDiv(conn, cidContDef, fila.get(1));
                }
                fila.clear();
                resp = true;
            }
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (!fila.isEmpty()) {
                fila.clear();
            }
            if (itr != null) {
                itr.remove();
            }
            pstm = null;
            query = null;
            fila = null;
            itr = null;
        }
        return resp;
    }

    private void updateFechaFormalizacionContrato(Connection conn, String cidcontratoDef, String cFechaForm) throws SQLException {
        String query = "";
        PreparedStatement pstm = null;
        try {
            query = "update mContrato set fFormalizacion=convert(date,'" + cFechaForm + "') where cIdContratoDefinitivo='" + cidcontratoDef + "'";
            pstm = conn.prepareStatement(query);
            pstm.executeUpdate();
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
    }

    private void updateFechaFormalizacionpContratoDiv(Connection conn, String cidcontratoDef, String cFechaForm) throws SQLException {
        String query = "";
        PreparedStatement pstm = null;
        try {
            query = "update pContratoDiverso set fFirmaContrato=convert(date,'" + cFechaForm + "') where cIdContrato='" + cidcontratoDef + "'";
            pstm = conn.prepareStatement(query);
            pstm.executeUpdate();
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
    }

    public boolean actualizamProcedimiento(Connection conn, String cIdProcedimiento, int nIdFundamentoLeg, int nIdCategoria) throws SQLException {
        String query = "";
        PreparedStatement pstm = null;
        boolean resp = false;
        try {
            query = "update mProcedimiento set nIdFundamentoLeg=" + nIdFundamentoLeg + ",nIdCategoria=" + nIdCategoria + " WHERE cIdProcedimiento='" + cIdProcedimiento + "'";
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            pstm.executeUpdate();
            resp = true;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
        return resp;
    }

    public boolean actualizamProcedimientoAdjudicacion(Connection conn, String cIdProcedimiento, int nIdFundamentoLeg, String cIdRFC, int nIdConsecutivoAdj) throws SQLException {
        String query = "";
        PreparedStatement pstm = null;
        boolean resp = false;
        try {
            query = "update mProcedimientoAdjudicacion set nIdFundamentoLeg=" + nIdFundamentoLeg + " WHERE cIdProcedimiento='" + cIdProcedimiento + "' and cIdRFC='" + cIdRFC + "' and nIdconsecutivoAdj=" + nIdConsecutivoAdj;
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            pstm.executeUpdate();
            resp = true;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
        return resp;
    }

    public boolean validaTipoProcedPorMonto(Connection conn, int nIdCategoria, double mMontoNeto) throws SQLException {
        String query = "";
        PreparedStatement pstm = null;
        ResultSet rs = null;
        boolean resp = false;
        try {
            query = "SELECT * FROM dbo.mCatalogoCategoriaProcedimiento WITH(NOLOCK) WHERE " + mMontoNeto + " BETWEEN mMontoMinimo AND mMontoMaximo AND nIdCategoria=" + nIdCategoria;
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            rs = pstm.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (rs != null) {
                rs.close();
            }
            pstm = null;
            rs = null;
            query = null;
        }
        return resp;
    }

    public int getNidConsecutivoAdj(Connection conn, String cIdcontratoDef) throws SQLException {
        String query = "";
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int resp = 0;
        try {
            query = "select nIdconsecutivoAdj from mContrato with(Nolock) where cIdContratoDefinitivo='" + cIdcontratoDef + "'";
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            rs = pstm.executeQuery();
            if (rs.next()) {
                resp = rs.getInt("nIdconsecutivoAdj");
            }
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (rs != null) {
                rs.close();
            }
            pstm = null;
            rs = null;
            query = null;
        }
        return resp;
    }

    public boolean validaContJustificado(Connection conn, DatosProcedimiento dat) throws SQLException {
        String query = "";
        PreparedStatement pstm = null;
        ResultSet rs = null;
        boolean resp = false;
        try {
            query = "select *from mContrato with(Nolock) where cidProcedimiento='" + dat.getcIdProcedimiento() + "' and  cIdContratoDefinitivo='" + dat.getcIdContratoDef() + "'";
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            rs = pstm.executeQuery();
            if (rs.next() && rs.getInt("lJustificaTipoProced") == 1) {
                resp = true;
            }
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (rs != null) {
                rs.close();
            }
            pstm = null;
            rs = null;
            query = null;
        }
        return resp;
    }

    public boolean validaMax50CentavosContAbierto(Connection conn, Map<String, String> map) throws SQLException {
        String query = "";
        PreparedStatement pstm = null;
        ResultSet rs = null;
        boolean resp = false;
        try {
            query = "SELECT ABS(SUM(mMontoNeto)-" + map.get("mMontoNetoMod") + ")modifica FROM dbo.mContratoAmpliacionLineas WITH(NOLOCK) WHERE cEjercicio='" + map.get("cEjercicio") + "' AND cIdTipoContrato='" + map.get("cIdTipoContrato") + "' AND cIdUnidadEjecutora='" + map.get("cIdUnidadEjecutora") + "' AND nIdConsecutivo=" + map.get("nIdConsecutivo") + " AND nIdConsecutivoAmpliacion=" + map.get("nIdConsecutivoAmpliacion");
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            rs = pstm.executeQuery();
            if (rs.next() && rs.getDouble("modifica") <= 0.5) {
                resp = true;
            }
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (rs != null) {
                rs.close();
            }
            pstm = null;
            rs = null;
            query = null;
        }
        return resp;
    }
}
