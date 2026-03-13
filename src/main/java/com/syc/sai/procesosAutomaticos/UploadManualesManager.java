package com.syc.sai.procesosAutomaticos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.syc.contable.core.AdecuacionManager;
import com.syc.contable.core.ManualContable;
import com.syc.contable.core.ManualContableDetalle;
import com.syc.contable.core.ManualContableEncabezado;
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

public class UploadManualesManager {

    private static final Logger log = LoggerFactory.getLogger(UploadManualesManager.class);

    public static Caso creaManualContable(Connection conn, Usuario u, int idTipoCaso, String centroContable, FolioGeneratorInterface fg) throws Exception {
        //OK
        /* Crea caso y expedientes */
        //OK
        Caso c = CasoManager.nuevoCaso(conn, u, idTipoCaso, fg);
        //OK
        c = UploadManualesManager.creaEstrucDoctosManualContable(conn, c, u);
        CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
        co.setIdOperacion(2);
        co.setResponsable("CONSULTA_MANUALCONTABLE");
        CasoOperacionManager.update(conn, co);
        /* Crea documento */
        int nFolioConciliacion = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
        ManualContableEncabezado cbfe = new ManualContableEncabezado(centroContable, "", nFolioConciliacion, c.getFolio(), c.getIdGabinete());
        List<ManualContableDetalle> detalle = new ArrayList<ManualContableDetalle>();
        ManualContable conCon = new ManualContable(cbfe, detalle);
        UploadManualesManager.insertar(conn, conCon);
        /* Devuelve el tramite creado */
        return c;
    }

    private static Caso creaEstrucDoctosManualContable(Connection conn, Caso c, Usuario u) throws Exception {
        //OK
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

    public static boolean existeManualContableCC(Connection conn, String centroContable, int manual, int nIDversion) throws Exception {
        //OK
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean retVal = false;
        String query = "SELECT COUNT(*) AS Existe FROM tManualContableEncabezado ENC\r\n" + "	JOIN tManualContableDetalle DET ON ENC.nFolioManualContable = DET.nFolioManualContable \r\n" + "	WHERE cCentroContable = ? AND nIDManual = ? AND nIDversion = ?\r\n";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, centroContable);
            ps.setInt(2, manual);
            ps.setInt(3, nIDversion);
            rs = ps.executeQuery();
            if (rs.next())
                retVal = rs.getInt("Existe") > 0;
            return retVal;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static boolean existeVersionManual(ManualContable cCont, int idManual) {
        //OK
        boolean existe = false;
        for (Iterator<ManualContableDetalle> i = cCont.getDetalle().iterator(); i.hasNext(); ) {
            ManualContableDetalle detalle = i.next();
            if (idManual == detalle.getIdManual()) {
                existe = true;
                break;
            }
        }
        return existe;
    }

    public static int insertaDetalle(Connection conn, ManualContableDetalle detalle) throws Exception {
        //OK
        String query = "INSERT INTO tManualContableDetalle(nFolioManualContable, cUsuarioCarga, nIDManual, nIDVersion) " + " VALUES(?, ?, ?, ?)";
        PreparedStatement psInsert = null;
        int insertados = 0;
        try {
            psInsert = conn.prepareStatement(query);
            psInsert.setInt(1, detalle.getFolioManualContable());
            psInsert.setString(2, detalle.getUsuarioCarga());
            psInsert.setInt(3, detalle.getIdManual());
            psInsert.setInt(4, detalle.getIdVersion());
            insertados = psInsert.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    private static int insertaEncabezado(Connection conn, ManualContableEncabezado encabezado) throws Exception {
        //OK
        String query = "INSERT INTO tManualContableEncabezado( nFolioManualContable, cCentroContable, cFolioSAI, nIDGabinete ) " + " VALUES(?, ?, ?, ?)";
        PreparedStatement psInsert = null;
        int insertados = 0;
        try {
            psInsert = conn.prepareStatement(query);
            psInsert.setInt(1, encabezado.getfolioManualContable());
            psInsert.setString(2, encabezado.getCentroContable());
            psInsert.setString(3, encabezado.getFolioSAI());
            psInsert.setInt(4, encabezado.getIdGabinete());
            insertados = psInsert.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    private static int insertar(Connection conn, ManualContable conCon) throws Exception {
        //OK
        int insertados = 0;
        insertados += UploadManualesManager.insertaEncabezado(conn, conCon.getEncabezado());
        for (Iterator<ManualContableDetalle> i = conCon.getDetalle().iterator(); i.hasNext(); ) {
            insertados += UploadManualesManager.insertaDetalle(conn, i.next());
        }
        return insertados;
    }

    public static Caso readCasoOrigenManualContable(Connection conn, String centroContable, int idManual) throws Exception {
        //OK
        ResultSet rs = null;
        PreparedStatement ps = null;
        Caso c = null;
        String query = "SELECT ENC.cFolioSAI FROM tManualContableEncabezado ENC\r\n" + "	JOIN tManualContableDetalle DET ON ENC.nFolioManualContable = DET.nFolioManualContable \r\n" + "	WHERE cCentroContable = ? AND nIDManual= ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, centroContable);
            ps.setInt(2, idManual);
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

    public static ManualContable readManualContable(Connection conn, String folioSAI) throws Exception {
        //OK
        ManualContable cbf = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT cFolioSAI FROM tManualContableEncabezado WITH(nolock) WHERE cFolioSAI = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, folioSAI);
            rs = ps.executeQuery();
            if (rs.next()) {
                folioSAI = rs.getString(1);
                //OK
                ManualContableEncabezado cbfe = readManualContableEncabezado(conn, folioSAI);
                //OK
                List<ManualContableDetalle> cbfd = readManualContableDetalle(conn, cbfe.getfolioManualContable());
                //OK
                cbf = new ManualContable(cbfe, cbfd);
            }
            return cbf;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private static List<ManualContableDetalle> readManualContableDetalle(Connection conn, int folioManual) throws Exception {
        //OK
        List<ManualContableDetalle> detalle = new ArrayList<ManualContableDetalle>();
        String query = "SELECT	nFolioManualContable, " + " 		fCarga, " + " 		cUsuarioCarga, " + " 		nIDManual" + "  FROM	tManualContableDetalle WITH(NOLOCK) " + " WHERE	nFolioManualContable = ? ";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folioManual);
            rs = ps.executeQuery();
            while (rs.next()) {
                ManualContableDetalle aux = new ManualContableDetalle();
                aux.setFolioManualContable(rs.getInt("nFolioManualContable"));
                aux.setUsuarioCarga(rs.getString("cUsuarioCarga"));
                aux.setIdManual(rs.getInt("nIDManual"));
                detalle.add(aux);
            }
            return detalle;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private static ManualContableEncabezado readManualContableEncabezado(Connection conn, String folioSAI) throws Exception {
        //OK
        String query = " SELECT	nFolioManualContable , " + "		cCentroContable , " + "		cFolioSAI , " + "		nIDGabinete " + "  FROM	tManualContableEncabezado WITH(NOLOCK) " + " WHERE	cFolioSAI = ? ";
        ResultSet rs = null;
        PreparedStatement ps = null;
        ManualContableEncabezado cce = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, folioSAI);
            rs = ps.executeQuery();
            if (rs.next()) {
                cce = new ManualContableEncabezado(rs.getString("cCentroContable"), "", rs.getInt("nFolioManualContable"), rs.getString("cFolioSAI"), rs.getInt("nIDGabinete"));
            }
            return cce;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static int ObtenerIDManual(Connection conn, String nombreCarpeta) throws Exception {
        //OK
        PreparedStatement ps = null;
        ResultSet rs = null;
        int idManual = -1;
        String query = " SELECT nIDManual FROM tCatTipoManualCont WHERE cNombreManual = ? ";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, nombreCarpeta);
            rs = ps.executeQuery();
            if (rs.next()) {
                idManual = rs.getInt("nIDManual");
            }
            return idManual;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }
}
