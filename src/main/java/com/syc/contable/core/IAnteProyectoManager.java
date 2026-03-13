package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.CasoOperacionBusinessLogic;
import com.syc.gestion.EmpleadoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.Empleado;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IAnteProyectoManager {

    private static Logger log = LoggerFactory.getLogger(IAnteProyectoManager.class);

    public static void insertaIAnteproyectoEncabezado(Connection conn, IAnteproyectoEncabezado enc) throws SQLException {
        PreparedStatement pstm = null;
        PreparedStatement pstms = null;
        ResultSet rs = null;
        int iExiste = 0;
        String cQueryExiste = "Select count(*) from tIntAnteProyectoEncabezado with (nolock) where nFolioAnteProyecto = ? AND aEjercicioFiscal = ? AND cUnidadResponsable = ? ";
        // String
        // cQueryExiste="Select count(*) from tAnteProyectoEncabezado with (nolock) where nFolioAnteProyecto = ? AND aEjercicioFiscal = ? ";
        String cQueryCrea = "INSERT INTO tIntAnteProyectoEncabezado (nFolioAnteProyecto ,aEjercicioFiscal, cArea, cUnidadResponsable " + " ,fCarga, nCuenta, nIncremento" + " ,nDecremento, cDescripcion, cUnidadNormativa, id_caso, bArea, bDL, bOC, bUN, motivoRechazo, responsable, responsableInt,bIntegrado) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            pstms = conn.prepareStatement(cQueryExiste);
            pstms.setInt(1, enc.getFolio());
            pstms.setString(2, enc.getaEjercicioFiscal());
            pstms.setString(3, enc.getcUnidadResponsable());
            rs = pstms.executeQuery();
            if (rs.next()) {
                iExiste = rs.getInt(1);
            }
            if (iExiste == 0) {
                pstm = conn.prepareStatement(cQueryCrea);
                pstm.setInt(1, enc.getFolio());
                pstm.setString(2, enc.getaEjercicioFiscal());
                pstm.setString(3, enc.getcArea());
                pstm.setString(4, enc.getcUnidadResponsable());
                pstm.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                pstm.setString(6, enc.getnCuenta());
                pstm.setInt(7, enc.getnIncremento());
                pstm.setInt(8, enc.getnDecremento());
                pstm.setString(9, enc.getcDescripcion());
                pstm.setString(10, enc.getcUnidadNormativa());
                pstm.setInt(11, enc.getId_caso());
                pstm.setString(12, enc.getbArea());
                pstm.setString(13, enc.getbDL());
                pstm.setString(14, enc.getbOC());
                pstm.setString(15, enc.getbUN());
                pstm.setString(16, enc.getMotivoRechazo());
                pstm.setString(17, enc.getResponsable());
                pstm.setString(18, enc.getResponsableInt());
                pstm.setString(19, enc.getbIntegrado());
                pstm.execute();
            }
        } finally {
            if (pstm != null)
                pstm.close();
            pstm = null;
            if (pstms != null)
                pstms.close();
            pstms = null;
            if (rs != null)
                rs.close();
            rs = null;
        }
    }

    public static int insertaRenglonValidacionAnteProyecto(Connection conn, Map<String, String> infoRenglon) throws Exception {
        Statement stmnt = null;
        int r = 0;
        try {
            log.trace("Iniciando insercion de renglon");
            stmnt = conn.createStatement();
            r = stmnt.executeUpdate(Util.genInsertFromMap("tcorrida_validacion_anteproyecto", infoRenglon));
            log.trace("Se inserto " + r + "registros");
            return r;
        } finally {
            CloseObject.closeObject(stmnt, false);
        }
    }

    public static List<String> validacionAnteProyectoCatalogos(Connection conn, int folio) throws Exception {
        Statement stmnt = null;
        ResultSet rs = null;
        List<String> r = new ArrayList<String>();
        try {
            String sql = " DECLARE @folio int, @mensajeO varchar(500) EXEC sp_ValidaAnteProyecto " + folio + ", @mensaje=@mensajeO OUTPUT";
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(sql);
            while (rs.next()) {
                r.add(rs.getString("msg"));
            }
        } finally {
            CloseObject.closeObject(stmnt, false);
        }
        return r;
    }

    public static List<String> validacionAnteProyectoDuplicidad(Connection conn, int folio) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<String> r = new ArrayList<String>();
        try {
            String sql = " select 'La EP '+EP+' se encuentra repetida '+convert(varchar,COUNT(*))+' veces, favor de agruparla en una sola' msg from tcorrida_validacion_anteproyecto WITH(NOLOCK)" + "	where nFolioAnteProyecto=?" + "	group by EP" + "	having COUNT(*)>1";
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, folio);
            rs = pstm.executeQuery();
            while (rs.next()) {
                r.add(rs.getString("msg"));
            }
        } finally {
            CloseObject.closeObject(pstm, false);
        }
        return r;
    }

    public static List<String> validacionAnteProyectoUR(Connection conn, int folio, String UR) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<String> r = new ArrayList<String>();
        try {
            String sql = " 	select 'La EP '+EP+' no corresponde a la unidad del usuario' msg from v_valida_anteproyecto_ep WITH(NOLOCK)" + "	where nFolioAnteProyecto=? AND ue!=?";
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, folio);
            pstm.setString(2, UR);
            rs = pstm.executeQuery();
            while (rs.next()) {
                r.add(rs.getString("msg"));
            }
        } finally {
            CloseObject.closeObject(pstm, false);
        }
        return r;
    }

    public static String integrado(Connection conn, String UR, String grupoInt, int id_oper) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String r = "";
        try {
            String sql = " 		select C_FOLIO from CG_CASO ca, CG_CASO_OPERACION co" + "	where ca.ID_CASO=co.ID_CASO AND C_FOLIO like 'IANT-" + UR + "%' AND co.id_oper=?";
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, id_oper);
            rs = pstm.executeQuery();
            if (rs.next()) {
                r = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(pstm, false);
        }
        return r;
    }

    public static void borraTCorrida(Connection conn, int folio) throws SQLException {
        PreparedStatement pstmnt = null;
        try {
            String query = "DELETE FROM tcorrida_validacion_anteproyecto WHERE nFolioAnteProyecto = ?";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, folio);
            pstmnt.execute();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
    }

    public static void insertaIAnteproyectoValidado(Connection conn, int folio) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String cQueryCrea = "	INSERT INTO tintanteproyectodetalle " + "	(nconsecutivo, " + "	nfolioanteproyecto, " + "	aejerciciofiscal, " + "	cunidadresponsable, " + "	cclavesiaff, " + "	cclaveinterna, " + "	mcalculado, " + "	moptimo, " + "	mireductible) " + "		SELECT consecutivo, " + "		validacion.nfolioanteproyecto, " + "		encabezado.aejerciciofiscal, " + "		encabezado.cunidadresponsable, " + "		Substring(validacion.ep, 1, 55) AS siaff, " + "		Substring(validacion.ep, 57, 7) AS interna, " + "		validacion.mcalculado, " + "		validacion.moptimo, " + "		validacion.mireductible " + "	FROM   tcorrida_validacion_anteproyecto validacion, " + "		tintanteproyectoencabezado encabezado " + "WHERE  validacion.nfolioanteproyecto = ?" + "	AND validacion.nfolioanteproyecto = encabezado.nfolioanteproyecto";
        try {
            pstm = conn.prepareStatement(cQueryCrea);
            pstm.setInt(1, folio);
            pstm.execute();
        } finally {
            if (pstm != null)
                pstm.close();
            pstm = null;
            if (rs != null)
                rs.close();
            rs = null;
        }
    }

    public static void insertaIntegraCaptura(Connection conn, int folioIntegra, int folioCaptura) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String cQueryCrea = "	INSERT INTO tIntAnteProyectoIntCap VALUES(?,?)";
        try {
            pstm = conn.prepareStatement(cQueryCrea);
            pstm.setInt(1, folioIntegra);
            pstm.setInt(2, folioCaptura);
            pstm.execute();
        } finally {
            if (pstm != null)
                pstm.close();
            pstm = null;
            if (rs != null)
                rs.close();
            rs = null;
        }
    }

    /**
     * Para un caso dispersa segun su detalle en tantos casos como unidades
     * normativas tenga
     *
     * @param conn
     *            Conexion activa a la base de datos.
     * @param nFolio
     *            Folio a dispersar
     * @return Numero de registros insertados
     * @throws Exception
     */
    public static Caso dispersaIntegracionUN(Connection conn, int nFolio, Usuario u, FolioGeneratorInterface fg, String jniName, IAnteproyectoEncabezado enc, String fCarga) throws Exception {
        String query = "SELECT DISTINCT Substring(EP, 61, 3) AS UN" + " FROM   v_IntAnteProyectoIntegradoDetalle " + " WHERE  nfolioanteproyecto = ? ";
        PreparedStatement ps = null;
        ResultSet rsCasos = null;
        String unidadEjecutoraOriginal = u.getU_UR();
        Caso c = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolio);
            rsCasos = ps.executeQuery();
            while (rsCasos.next()) {
                String un = rsCasos.getString("UN");
                log.info(un);
                u.setU_UR(un);
                c = generaCaso(u, 40, fg, "Integracion de Anteproyecto UN " + un, jniName, un);
                enc.setFolio(new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue());
                enc.setId_caso(c.getIdCaso());
                insertaIntegraUN(conn, un, nFolio, new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), enc, fCarga);
            }
            return c;
        } finally {
            CloseObject.closeObject(rsCasos, false);
            CloseObject.closeObject(ps, false);
            u.setU_UR(unidadEjecutoraOriginal);
        }
    }

    public static void insertaIntegraUN(Connection conn, String un, int nFolio, int folioNuevoCaso, IAnteproyectoEncabezado enc, String fCarga) throws Exception {
        String queryIE = "INSERT INTO tIntAnteProyectoEncabezado(nFolioAnteProyecto,aEjercicioFiscal,cArea,cUnidadResponsable,fCarga,cUnidadNormativa,id_caso,responsable,responsableInt,bIntegrado) VALUES(?,?,?,?,?,?,?,?,?,?)";
        String queryID = "INSERT INTO tIntAnteProyectoDetalle(nConsecutivo,nFolioAnteProyecto,aEjercicioFiscal,cUnidadResponsable,cClaveSIAFF,cClaveInterna,mCalculado,mOptimo,mIreductible) select DISTINCT ROW_NUMBER() OVER (ORDER BY ep ) as nCosecutivo, " + String.valueOf(folioNuevoCaso) + " as nFolioAnteProyecto, aEjercicioFiscal,cUnidadResponsable,Substring(EP, 1, 55) as cClaveSIAFF,Substring(EP,57,7) as cClaveInterna,mCalculado,mOptimo,mIreductible from v_IntAnteProyectoIntegradoDetalle where nFolioAnteProyecto=? AND Substring(EP, 61, 3) = ?";
        PreparedStatement ps = null;
        PreparedStatement psID = null;
        try {
            ps = conn.prepareStatement(queryIE);
            ps.setInt(1, folioNuevoCaso);
            ps.setString(2, enc.getaEjercicioFiscal());
            ps.setString(3, enc.getcArea());
            ps.setString(4, enc.getcUnidadResponsable());
            ps.setString(5, fCarga);
            ps.setString(6, enc.getcUnidadNormativa());
            ps.setInt(7, enc.getId_caso());
            ps.setString(8, enc.getResponsable());
            ps.setString(9, enc.getResponsableInt());
            ps.setString(10, enc.getbIntegrado());
            ps.execute();
            psID = conn.prepareStatement(queryID);
            psID.setInt(1, nFolio);
            psID.setString(2, un);
            psID.execute();
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(psID, false);
        }
    }

    private static Caso generaCaso(Usuario u, int idTCaso, FolioGeneratorInterface fg, String concepto, String jniName, String UN) throws GestionException, SQLException, Exception {
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
        Caso c = casoTx.IniciaCaso(u, idTCaso, fg);
        String responsable = "INT_ANTPROYECTO_UN_" + UN;
        AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(jniName);
        CasoOperacion co = c.getCasoOperacion(0);
        co.setIdOperacion(2);
        co.setResponsable(responsable);
        CasoOperacionBusinessLogic cobl = new CasoOperacionBusinessLogic(jniName);
        cobl.updateCasoOperacion(co);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        Empleado e = new Empleado();
        EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
        e.setClaveUsuario(u.getLogin());
        e = ebl.getEmpleado(e);
        // Variables del caso
        c.getCasoDato("FOLIO").setValor(c.getFolio());
        c.getCasoDato("OPERADOR").setValor(u.getLogin());
        c.getCasoDato("FECHA_DOCUMENTO").setValor(fecha);
        c.getCasoDato("EJERCICIO_FISCAL").setValor(adecProy.obtenEjercicioFiscal());
        c.getCasoDato("CONCEPTO_MOV").setValor(concepto);
        c.getCasoDato("MONEDA").setValor("MXP");
        c.getCasoDato("ID_AREA_ORIGEN").setValor(e.getClaveArea());
        c.getCasoDato("ID_DL_ORIGEN").setValor(u.getU_UR());
        Map<String, String> m = new HashMap<String, String>();
        m.put("FOLIO", c.getFolio());
        m.put("OPERADOR", u.getLogin());
        m.put("FECHA_DOCUMENTO", fecha);
        m.put("EJERCICIO_FISCAL", adecProy.obtenEjercicioFiscal());
        m.put("CONCEPTO_MOV", "Integracion Anteproyecto");
        m.put("MONEDA", "MXP");
        m.put("ID_AREA_ORIGEN", e.getClaveArea());
        m.put("ID_DL_ORIGEN", u.getU_UR());
        c.setIdGabinete(casoTx.creaExpediente(u.getLogin(), c));
        // Guarda las variables de caso.
        c = casoTx.actualizaCasoDato(c, m);
        return c;
    }
}
