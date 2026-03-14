package com.syc.sai.procesosAutomaticos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import com.syc.contable.core.AdecuacionManager;
import com.syc.contable.core.ConciliacionBancoFirmada;
import com.syc.contable.core.ConciliacionBancoFirmadaDetalle;
import com.syc.contable.core.ConciliacionBancoFirmadaEncabezado;
import com.syc.contable.core.ConciliacionContable;
import com.syc.contable.core.ConciliacionContableDetalle;
import com.syc.contable.core.ConciliacionContableEncabezado;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class UploadConciliacionManager {

    private static final Logger log = LoggerFactory.getLogger(UploadConciliacionManager.class);

    public static void borraConciliacionMes(Connection conn, ConciliacionBancoFirmada cbf, int nMes, boolean esEdoCta) throws Exception {
        String query = "DELETE FROM tConciliacionFirmadaDetalle WHERE nFolioConciliacionFirmada = ? AND nMes = ? AND lEsEdoCta = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, cbf.getEncabezado().getFolioConciliacionFirmada());
            ps.setInt(2, nMes);
            ps.setString(3, esEdoCta ? "S" : "N");
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static Caso creaConciliacion(Connection conn, Usuario u, int idTipoCaso, String ctaBan, String centroContable, FolioGeneratorInterface fg) throws Exception {
        /* Crea caso y expedientes */
        Caso c = CasoManager.nuevoCaso(conn, u, idTipoCaso, fg);
        c = UploadConciliacionManager.creaEstrucDoctosConciliacion(conn, c, u, ctaBan);
        CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
        co.setIdOperacion(2);
        co.setResponsable("CONSULTA_PAGOS");
        CasoOperacionManager.update(conn, co);
        /* Crea documento */
        int nFolioConciliacion = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
        ConciliacionBancoFirmadaEncabezado cbfe = new ConciliacionBancoFirmadaEncabezado(centroContable, ctaBan, nFolioConciliacion, c.getFolio(), c.getIdGabinete());
        List<ConciliacionBancoFirmadaDetalle> detalle = new ArrayList<ConciliacionBancoFirmadaDetalle>();
        ConciliacionBancoFirmada cbf = new ConciliacionBancoFirmada(cbfe, detalle);
        UploadConciliacionManager.insertar(conn, cbf);
        /* Devuelve el tramite creado */
        return c;
    }

    public static Caso creaConciliacionContable(Connection conn, Usuario u, int idTipoCaso, String centroContable, FolioGeneratorInterface fg) throws Exception {
        /* Crea caso y expedientes */
        Caso c = CasoManager.nuevoCaso(conn, u, idTipoCaso, fg);
        c = UploadConciliacionManager.creaEstrucDoctosConciliacionContable(conn, c, u);
        CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
        co.setIdOperacion(2);
        co.setResponsable("CONSULTA_CONCILIACONTABLE");
        CasoOperacionManager.update(conn, co);
        /* Crea documento */
        int nFolioConciliacion = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
        ConciliacionContableEncabezado cbfe = new ConciliacionContableEncabezado(centroContable, "", nFolioConciliacion, c.getFolio(), c.getIdGabinete());
        List<ConciliacionContableDetalle> detalle = new ArrayList<ConciliacionContableDetalle>();
        ConciliacionContable conCon = new ConciliacionContable(cbfe, detalle);
        UploadConciliacionManager.insertar(conn, conCon);
        /* Devuelve el tramite creado */
        return c;
    }

    private static Caso creaEstrucDoctosConciliacion(Connection conn, Caso c, Usuario u, String ctaBan) throws Exception {
        if (c.getIdGabinete() == -1) {
            String cEjercicioFiscal = AdecuacionManager.obtenEjercicioFiscal(conn);
            c.getCasoDato("FOLIO").setValor(c.getFolio());
            c.getCasoDato("FECHA_DOCUMENTO").setValor(Util.getToday());
            c.getCasoDato("EJERCICIO_FISCAL").setValor(cEjercicioFiscal);
            c.getCasoDato("OPERADOR").setValor(u.getNombre());
            c.getCasoDato("CENTRO_CONTABLE").setValor(u.getPropiedad("CCENTROCONTABLE").getValor());
            c.getCasoDato("CTAB").setValor(ctaBan);
            Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
            int id_gabinete = AplicacionManager.createExpediente(conn, u.getLogin(), c, app);
            if (id_gabinete < 0) {
                log.error("Identificador de Gabiente invalido (< 0)");
                throw new SQLException("Identificador de Gabiente inválido (< 0)");
            }
            c.setIdGabinete(id_gabinete);
            CasoManager.update(conn, c);
        }
        return c;
    }

    private static Caso creaEstrucDoctosConciliacionContable(Connection conn, Caso c, Usuario u) throws Exception {
        if (c.getIdGabinete() == -1) {
            String cEjercicioFiscal = AdecuacionManager.obtenEjercicioFiscal(conn);
            c.getCasoDato("FOLIO").setValor(c.getFolio());
            c.getCasoDato("FECHA_DOCUMENTO").setValor(Util.getToday());
            c.getCasoDato("EJERCICIO_FISCAL").setValor(cEjercicioFiscal);
            c.getCasoDato("OPERADOR").setValor(u.getNombre());
            c.getCasoDato("CENTRO_CONTABLE").setValor(u.getPropiedad("CCENTROCONTABLE").getValor());
            Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
            int id_gabinete = AplicacionManager.createExpediente(conn, u.getLogin(), c, app);
            if (id_gabinete < 0) {
                log.error("Identificador de Gabiente invalido (< 0)");
                throw new SQLException("Identificador de Gabiente inválido (< 0)");
            }
            c.setIdGabinete(id_gabinete);
            CasoManager.update(conn, c);
        }
        return c;
    }

    public static boolean existeConcilicacion(Connection conn, String ctaBan, String centroContable) throws Exception {
        String query = "SELECT	COUNT(*) AS Existe " + "  FROM	tConciliacionFirmadaEncabezado WITH(nolock) " + " WHERE	cCuentaBancaria = ? " + "   AND	cCentroContable = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean retVal = false;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, ctaBan);
            ps.setString(2, centroContable);
            rs = ps.executeQuery();
            if (rs.next())
                retVal = rs.getInt("Existe") > 0;
            return retVal;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static boolean existeConcilicacionContableCC(Connection conn, String centroContable) throws Exception {
        String query = "SELECT	COUNT(*) AS Existe " + "  FROM	tConciliacionContableEncabezado WITH(nolock) " + " WHERE	cCentroContable = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean retVal = false;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, centroContable);
            rs = ps.executeQuery();
            if (rs.next())
                retVal = rs.getInt("Existe") > 0;
            return retVal;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static boolean existeMesConciliacion(ConciliacionBancoFirmada cbf, int nMes, boolean esEdoCta) {
        boolean existe = false;
        for (Iterator<ConciliacionBancoFirmadaDetalle> i = cbf.getDetalle().iterator(); i.hasNext(); ) {
            ConciliacionBancoFirmadaDetalle detalle = i.next();
            if (nMes == detalle.getMes() && esEdoCta == detalle.isEdoCta()) {
                existe = true;
                break;
            }
        }
        return existe;
    }

    public static boolean existeMesConciliacion(ConciliacionContable cCont, int nMes, int idConciliacion) {
        boolean existe = false;
        for (Iterator<ConciliacionContableDetalle> i = cCont.getDetalle().iterator(); i.hasNext(); ) {
            ConciliacionContableDetalle detalle = i.next();
            if (nMes == detalle.getMes() && idConciliacion == detalle.getIdConciliacion()) {
                existe = true;
                break;
            }
        }
        return existe;
    }

    public static int insertaDetalle(Connection conn, ConciliacionBancoFirmadaDetalle detalle) throws Exception {
        String query = "INSERT INTO tConciliacionFirmadaDetalle(nFolioConciliacionFirmada, nMes, cUsuarioCarga, lEsEdoCta, mSaldoAnterior, mSaldoAlCorte) " + " VALUES(?, ?, ?, ?, ?, ?)";
        PreparedStatement psInsert = null;
        int insertados = 0;
        try {
            psInsert = conn.prepareStatement(query);
            psInsert.setInt(1, detalle.getFolioConciliacionFirmada());
            psInsert.setInt(2, detalle.getMes());
            psInsert.setString(3, detalle.getUsuarioCarga());
            psInsert.setString(4, detalle.isEdoCta() ? "S" : "N");
            if (detalle.getSaldo() != null) {
                psInsert.setDouble(5, detalle.getSaldo().getSaldoAnterior());
                psInsert.setDouble(6, detalle.getSaldo().getSaldoAlCorte());
            } else {
                psInsert.setDouble(5, 0.0d);
                psInsert.setDouble(6, 0.0d);
            }
            insertados = psInsert.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    public static int insertaDetalle(Connection conn, ConciliacionContableDetalle detalle) throws Exception {
        String query = "INSERT INTO tConciliacionContableDetalle(nFolioConciliacionContable, nMes, cUsuarioCarga, nIDConciliacion) " + " VALUES(?, ?, ?, ?)";
        PreparedStatement psInsert = null;
        int insertados = 0;
        try {
            psInsert = conn.prepareStatement(query);
            psInsert.setInt(1, detalle.getFolioConciliacionContable());
            psInsert.setInt(2, detalle.getMes());
            psInsert.setString(3, detalle.getUsuarioCarga());
            psInsert.setInt(4, detalle.getIdConciliacion());
            insertados = psInsert.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    public static int insertaEncabezado(Connection conn, ConciliacionBancoFirmadaEncabezado encabezado) throws Exception {
        String query = "INSERT INTO tConciliacionFirmadaEncabezado( nFolioConciliacionFirmada, cCuentaBancaria, cCentroContable, cFolioSAI, nIDGabinete ) " + " VALUES(?, ?, ?, ?, ?)";
        PreparedStatement psInsert = null;
        int insertados = 0;
        try {
            psInsert = conn.prepareStatement(query);
            psInsert.setInt(1, encabezado.getFolioConciliacionFirmada());
            psInsert.setString(2, encabezado.getCuentaBancaria());
            psInsert.setString(3, encabezado.getCentroContable());
            psInsert.setString(4, encabezado.getFolioSAI());
            psInsert.setInt(5, encabezado.getIdGabinete());
            insertados = psInsert.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    private static int insertaEncabezado(Connection conn, ConciliacionContableEncabezado encabezado) throws Exception {
        String query = "INSERT INTO tConciliacionContableEncabezado( nFolioConciliacionContable, cCuentaBancaria, cCentroContable, cFolioSAI, nIDGabinete ) " + " VALUES(?, ?, ?, ?, ?)";
        PreparedStatement psInsert = null;
        int insertados = 0;
        try {
            psInsert = conn.prepareStatement(query);
            psInsert.setInt(1, encabezado.getfolioConciliacionContable());
            psInsert.setString(2, encabezado.getCuentaBancaria());
            psInsert.setString(3, encabezado.getCentroContable());
            psInsert.setString(4, encabezado.getFolioSAI());
            psInsert.setInt(5, encabezado.getIdGabinete());
            insertados = psInsert.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    private static int insertar(Connection conn, ConciliacionBancoFirmada cbf) throws Exception {
        int insertados = 0;
        insertados += UploadConciliacionManager.insertaEncabezado(conn, cbf.getEncabezado());
        for (Iterator<ConciliacionBancoFirmadaDetalle> i = cbf.getDetalle().iterator(); i.hasNext(); ) {
            insertados += UploadConciliacionManager.insertaDetalle(conn, i.next());
        }
        return insertados;
    }

    private static int insertar(Connection conn, ConciliacionContable conCon) throws Exception {
        int insertados = 0;
        insertados += UploadConciliacionManager.insertaEncabezado(conn, conCon.getEncabezado());
        for (Iterator<ConciliacionContableDetalle> i = conCon.getDetalle().iterator(); i.hasNext(); ) {
            insertados += UploadConciliacionManager.insertaDetalle(conn, i.next());
        }
        return insertados;
    }

    public static ConciliacionBancoFirmada read(Connection conn, int idGabinete) throws Exception {
        ConciliacionBancoFirmada cbf = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT cFolioSAI FROM tConciliacionFirmadaEncabezado WITH(nolock) WHERE nIDGabinete = ?";
        String folioSAI = "";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, idGabinete);
            rs = ps.executeQuery();
            if (rs.next()) {
                folioSAI = rs.getString(1);
                ConciliacionBancoFirmadaEncabezado cbfe = readConciliacionEncabezado(conn, folioSAI);
                List<ConciliacionBancoFirmadaDetalle> cbfd = readConciliacionDetalle(conn, cbfe.getFolioConciliacionFirmada());
                cbf = new ConciliacionBancoFirmada(cbfe, cbfd);
            }
            return cbf;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static ConciliacionBancoFirmada read(Connection conn, String folioSAI) throws Exception {
        ConciliacionBancoFirmada cbf = null;
        ConciliacionBancoFirmadaEncabezado cbfe = readConciliacionEncabezado(conn, folioSAI);
        List<ConciliacionBancoFirmadaDetalle> cbfd = readConciliacionDetalle(conn, cbfe.getFolioConciliacionFirmada());
        cbf = new ConciliacionBancoFirmada(cbfe, cbfd);
        return cbf;
    }

    public static Caso readCasoOrigen(Connection conn, String ctaBan, String centroContable) throws Exception {
        String query = "SELECT	cFolioSAI " + "  FROM	tConciliacionFirmadaEncabezado WITH(NOLOCK) " + " WHERE	cCuentaBancaria = ? " + "   AND	cCentroContable = ? ";
        ResultSet rs = null;
        PreparedStatement ps = null;
        Caso c = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, ctaBan);
            ps.setString(2, centroContable);
            rs = ps.executeQuery();
            if (rs.next()) {
                String cFolio = rs.getString(1);
                c = new Caso();
                c.setFolio(cFolio);
                c = CasoManager.select(conn, c);
            }
            return c;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static Caso readCasoOrigenConciliacionContable(Connection conn, String centroContable) throws Exception {
        String query = "SELECT	cFolioSAI " + "  FROM	tConciliacionContableEncabezado WITH(NOLOCK) " + " WHERE	cCentroContable = ? ";
        ResultSet rs = null;
        PreparedStatement ps = null;
        Caso c = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, centroContable);
            rs = ps.executeQuery();
            if (rs.next()) {
                String cFolio = rs.getString(1);
                c = new Caso();
                c.setFolio(cFolio);
                c = CasoManager.select(conn, c);
            }
            return c;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static ConciliacionContable readConciliacionContable(Connection conn, String folioSAI) throws Exception {
        ConciliacionContable cbf = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT cFolioSAI FROM tConciliacionContableEncabezado WITH(nolock) WHERE cFolioSAI = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, folioSAI);
            rs = ps.executeQuery();
            if (rs.next()) {
                folioSAI = rs.getString(1);
                ConciliacionContableEncabezado cbfe = readConciliacionContableEncabezado(conn, folioSAI);
                List<ConciliacionContableDetalle> cbfd = readConciliacionContableDetalle(conn, cbfe.getfolioConciliacionContable());
                cbf = new ConciliacionContable(cbfe, cbfd);
            }
            return cbf;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private static List<ConciliacionContableDetalle> readConciliacionContableDetalle(Connection conn, int folioConciliacion) throws Exception {
        List<ConciliacionContableDetalle> detalle = new ArrayList<ConciliacionContableDetalle>();
        String query = "SELECT	nFolioConciliacionContable, " + " 		nMes, " + " 		fCarga, " + " 		cUsuarioCarga, " + " 		nIDConciliacion " + "  FROM	tConciliacionContableDetalle WITH(NOLOCK) " + " WHERE	nFolioConciliacionContable = ? ";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folioConciliacion);
            rs = ps.executeQuery();
            while (rs.next()) {
                ConciliacionContableDetalle aux = new ConciliacionContableDetalle();
                aux.setFolioConciliacionContable(rs.getInt("nFolioConciliacionContable"));
                aux.setMes(rs.getInt("nMes"));
                aux.setUsuarioCarga(rs.getString("cUsuarioCarga"));
                aux.setIdConciliacion(rs.getInt("nIDConciliacion"));
                detalle.add(aux);
            }
            return detalle;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private static ConciliacionContableEncabezado readConciliacionContableEncabezado(Connection conn, String folioSAI) throws Exception {
        String query = " SELECT	nFolioConciliacionContable , " + "		cCuentaBancaria , " + "		cCentroContable , " + "		cFolioSAI , " + "		nIDGabinete " + "  FROM	tConciliacionContableEncabezado WITH(NOLOCK) " + " WHERE	cFolioSAI = ? ";
        ResultSet rs = null;
        PreparedStatement ps = null;
        ConciliacionContableEncabezado cce = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, folioSAI);
            rs = ps.executeQuery();
            if (rs.next()) {
                cce = new ConciliacionContableEncabezado(rs.getString("cCentroContable"), "", rs.getInt("nFolioConciliacionContable"), rs.getString("cFolioSAI"), rs.getInt("nIDGabinete"));
            }
            return cce;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private static List<ConciliacionBancoFirmadaDetalle> readConciliacionDetalle(Connection conn, int folioConciliacion) throws Exception {
        List<ConciliacionBancoFirmadaDetalle> detalle = new ArrayList<ConciliacionBancoFirmadaDetalle>();
        String query = "SELECT	nFolioConciliacionFirmada, " + "		nMes, " + "		fCarga, " + "		cUsuarioCarga," + "         lEsEdoCta, " + "         mSaldoAnterior, " + "         mSaldoAlCorte " + "  FROM	tConciliacionFirmadaDetalle WITH(NOLOCK) " + " WHERE	nFolioConciliacionFirmada = ? ";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folioConciliacion);
            rs = ps.executeQuery();
            while (rs.next()) {
                ConciliacionBancoFirmadaDetalle aux = new ConciliacionBancoFirmadaDetalle();
                aux.setFechaCarga(new Date(rs.getDate("fCarga").getTime()));
                aux.setFolioConciliacionFirmada(rs.getInt("nFolioConciliacionFirmada"));
                aux.setMes(rs.getInt("nMes"));
                aux.setUsuarioCarga(rs.getString("cUsuarioCarga"));
                aux.setEdoCta("S".equalsIgnoreCase(rs.getString("lEsEdoCta")));
                SaldosEdoCta saldo = new SaldosEdoCta(rs.getDouble("mSaldoAnterior"), rs.getDouble("mSaldoAlCorte"));
                aux.setSaldo(saldo);
                detalle.add(aux);
            }
            return detalle;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private static ConciliacionBancoFirmadaEncabezado readConciliacionEncabezado(Connection conn, String folioSAI) throws Exception {
        String query = " SELECT	nFolioConciliacionFirmada, " + "		cCuentaBancaria, " + "		cCentroContable, " + "		cFolioSAI, " + "		nIDGabinete " + "  FROM	tConciliacionFirmadaEncabezado WITH(NOLOCK) " + " WHERE	cFolioSAI = ? ";
        ResultSet rs = null;
        PreparedStatement ps = null;
        ConciliacionBancoFirmadaEncabezado cbfe = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, folioSAI);
            rs = ps.executeQuery();
            if (rs.next()) {
                cbfe = new ConciliacionBancoFirmadaEncabezado(rs.getString("cCentroContable"), rs.getString("cCuentaBancaria"), rs.getInt("nFolioConciliacionFirmada"), rs.getString("cFolioSAI"), rs.getInt("nIDGabinete"));
            }
            return cbfe;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }
}
