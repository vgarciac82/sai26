package com.syc.contable.anteproyecto;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * Clase manager encargada de la carga de proyecto desde un archivo excel.
 *
 * @author Vicente Garcia Carrillo
 * @version 1.0
 */
public class CargaProyectoManager {

    private static Logger log = LoggerFactory.getLogger(CargaProyectoManager.class);

    /**
     * Valida que se han cargado Estructuras Programaticas de calendario con
     * capitulos restringidos.
     *
     * @param conn
     *            Conexion activa a la base de datos
     * @param esAdministrador
     *            Indica si el usuario es administrador
     * @param ue
     *            Unidad Ejecutora
     * @param excluir
     *            Lista de partidas restringidas.
     * @param ejercicioFiscal
     *            Ejercicio Fiscal
     * @return true si se encuentran EPs con capitulos restringidos a
     *         administrador.
     * @throws Exception
     */
    public static boolean calendarioCapitulosRestringidosCargado(Connection conn, boolean esAdministrador, String ue, int[] excluir, int ejercicioFiscal) throws Exception {
        String query = "SELECT COUNT(*) FROM vCalendario_Proyecto";
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean cargado = false;
        try {
            String token = " WHERE ";
            for (int i = 0; i < excluir.length; i++) {
                query += token + " cPartida LIKE '" + excluir[i] + "%'";
                token = " OR ";
            }
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next())
                cargado = rs.getInt(1) > 0;
            return cargado;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    /**
     * Valida que la unidad ejecutora tenga capturado su calendario. Se excluyen
     * las EPs con capitulos restringidos ya que estos los captura la unidad
     * administradora.
     *
     * @param conn
     *            Conexion activa a la base de datos
     * @param esAdministrador
     *            indica si es un usuario administrador
     * @param ue
     *            Unidad ejecutora del usuario
     * @param capitulosExcluir
     *            Capitulos a excluir
     * @param ejercicioFiscal
     *            Ejercicio fiscal
     * @return true si se cuenta con calendario capturado.
     */
    public static boolean calendarioUECargado(Connection conn, boolean esAdministrador, String ue, int[] capitulosExcluir, int ejercicioFiscal) throws Exception {
        String query = "SELECT COUNT(*) AS TOTAL_CAPTURADOS from vCalendario_Proyecto WHERE cUnidadEjecutora = '" + ue + "'";
        String cond = "AND (";
        String token = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean capturado = false;
        try {
            if (capitulosExcluir != null && capitulosExcluir.length > 0) {
                for (int i = 0; i < capitulosExcluir.length; i++) {
                    cond += token = "cPartida NOT LIKE '" + capitulosExcluir[i] + "%'";
                    token = " OR ";
                }
                cond += ")";
                query += cond;
            }
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next())
                capturado = rs.getInt(1) > 0;
            return capturado;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static File generaArchivoCalendarioProyecto(Connection conn, boolean esAdministrador, String ue, int ejercicioFiscal, int[] capitulosExcluir, String nombreArchivo) throws Exception {
        Statement stmnt = null;
        ResultSet rs = null;
        String query = "SELECT	EP, " + "		MONTO AS monto_anual, " + "		CONVERT(int,'0') as monto_enero, " + "		CONVERT(int,'0') as monto_febrero, " + "		CONVERT(int,'0') as monto_marzo, " + "		CONVERT(int,'0') as monto_abril, " + "		CONVERT(int,'0') as monto_mayo, " + "		CONVERT(int,'0') as monto_junio, " + "		CONVERT(int,'0') as monto_julio, " + "		CONVERT(int,'0') as monto_agosto, " + "		CONVERT(int,'0') as monto_septiembre, " + "		CONVERT(int,'0') as monto_octubre, " + "		CONVERT(int,'0') as monto_noviembre, " + "		CONVERT(int,'0') as monto_diciembre " + " FROM	tProyecto_PF " + " WHERE	aEjercicioFiscal = " + ejercicioFiscal + " AND cUnidadEjecutora = '" + ue + "' ";
        try {
            if (capitulosExcluir != null && !esAdministrador) {
                for (int i = 0; i < capitulosExcluir.length; i++) {
                    query += " AND cPartida NOT LIKE '" + capitulosExcluir[i] + "%' ";
                }
            }
            // Si es administrador debe incluir las partidas que fueron
            // exlcuidas de otras UE
            if (esAdministrador && capitulosExcluir != null && capitulosExcluir.length > 0) {
                String token = "";
                query += " UNION " + " SELECT	EP, " + "		MONTO AS monto_anual, " + "		CONVERT(int,'0') as monto_enero, " + "		CONVERT(int,'0') as monto_febrero, " + "		CONVERT(int,'0') as monto_marzo, " + "		CONVERT(int,'0') as monto_abril, " + "		CONVERT(int,'0') as monto_mayo, " + "		CONVERT(int,'0') as monto_junio, " + "		CONVERT(int,'0') as monto_julio, " + "		CONVERT(int,'0') as monto_agosto, " + "		CONVERT(int,'0') as monto_septiembre, " + "		CONVERT(int,'0') as monto_octubre, " + "		CONVERT(int,'0') as monto_noviembre, " + "		CONVERT(int,'0') as monto_diciembre " + " FROM	tProyecto_PF " + " WHERE	cUnidadEjecutora <> '" + ue + "' ";
                query += " AND ( ";
                for (int i = 0; i < capitulosExcluir.length; i++) {
                    query += token + " cPartida LIKE '" + capitulosExcluir[i] + "%' ";
                    token = " OR ";
                }
                query += ")";
            }
            query += " ORDER BY 1 ";
            log.trace("Object: {}", query.toString());
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(query);
            return Util.ExcelFromRS(rs, nombreArchivo);
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(stmnt, false);
        }
    }

    /**
     * Genera un archivo con el total del Proyecto Calendarizado al momento.
     *
     * @param conn
     *            Conexion activa a la base de datos.
     * @param unidadEjecutora
     *            Unidad Ejecutora
     * @param unidadNormativa
     *            Unidad Normativa
     * @param ejercicioFiscal
     *            Ejercicio Fiscal
     * @param nombreArchivo
     *            Nombre del archicvo a generar
     * @return Referencia al archivo creado.
     * @throws Exception
     */
    public static File generaArchivoCalendarioProyectoFinal(Connection conn, String unidadEjecutora, String unidadNormativa, int ejercicioFiscal, String nombreArchivo) throws Exception {
        Statement stmnt = null;
        ResultSet rs = null;
        String token = " WHERE ";
        String query = "SELECT	EP AS EP,  " + "		mMonto_ANUAL AS	monto_anual, " + "		mMonto_Enero AS	monto_enero, " + "		mMonto_Febrero AS monto_febrero, " + "		mMonto_Marzo AS monto_marzo, " + "		mMonto_Abril AS monto_abril,  " + "		mMonto_Mayo AS monto_mayo,  " + "		mMonto_Junio AS monto_junio, " + "		mMonto_Julio AS monto_julio,  " + "		mMonto_Agosto AS monto_agosto,  " + "		mMonto_Septiembre AS monto_septiembre, " + "		mMonto_Octubre AS monto_octubre,  " + "		mMonto_Noviembre AS monto_noviembre,  " + "		mMonto_Diciembre AS monto_diciembre " + " FROM	tProyecto_Calendario ";
        if (unidadNormativa != null && !"".equals(unidadNormativa)) {
            query += token + "cUnidadNormativa = '" + unidadNormativa.trim() + "'";
            token = " AND ";
        }
        if (unidadEjecutora != null && !"".equals(unidadEjecutora)) {
            query += token + "cUnidadEjecutora = '" + unidadEjecutora.trim() + "'";
            token = " AND ";
        }
        // if (ejercicioFiscal > 0)
        // query += token + "aEjercicioFiscal = " + ejercicioFiscal;
        query += " ORDER BY 1 ";
        try {
            log.trace("Object: {}", query.toString());
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(query);
            return Util.ExcelFromRS(rs, nombreArchivo);
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(stmnt, false);
        }
    }

    public static File generaArchivoProyecto(Connection conn, boolean esAdministrador, String ur, int ejercicioFiscal, int[] capitulosExcluir, String nombreArchivo) throws Exception {
        String query = "SELECT	EP, " + "	MONTO AS monto_anual, " + "	CONVERT(int,'0') as monto_enero, " + "	CONVERT(int,'0') as monto_febrero, " + "	CONVERT(int,'0') as monto_marzo, " + "	CONVERT(int,'0') as monto_abril, " + "	CONVERT(int,'0') as monto_mayo, " + "	CONVERT(int,'0') as monto_junio, " + "	CONVERT(int,'0') as monto_julio, " + "	CONVERT(int,'0') as monto_agosto, " + "	CONVERT(int,'0') as monto_septiembre, " + "	CONVERT(int,'0') as monto_octubre, " + "	CONVERT(int,'0') as monto_noviembre, " + "	CONVERT(int,'0') as monto_diciembre " + "  FROM	tProyecto_PF " + " WHERE	aEjercicioFiscal = " + ejercicioFiscal + (esAdministrador ? "" : " AND cUnidadEjecutora = '" + ur + "'");
        ResultSet rs = null;
        Statement stmnt = null;
        try {
            stmnt = conn.createStatement();
            if (!esAdministrador && capitulosExcluir != null && capitulosExcluir.length > 0)
                for (int i = 0; i < capitulosExcluir.length; i++) query += " AND cPartida NOT LIKE '" + capitulosExcluir[i] + "%' ";
            rs = stmnt.executeQuery(query);
            return Util.ExcelFromRS(rs, nombreArchivo);
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(stmnt, false);
        }
    }

    public static File generaArchivoProyectoPorUN(Connection conn, String unidadNormativa, String nombreArchivo, int ejercicioFiscal, int[] capitulosExcluir) throws Exception {
        String query = "SELECT EP, " + "	MONTO AS monto_anual, " + "	CONVERT(int,'0') as monto_enero, " + "	CONVERT(int,'0') as monto_febrero, " + "	CONVERT(int,'0') as monto_marzo, " + "	CONVERT(int,'0') as monto_abril, " + "	CONVERT(int,'0') as monto_mayo, " + "	CONVERT(int,'0') as monto_junio, " + "	CONVERT(int,'0') as monto_julio, " + "	CONVERT(int,'0') as monto_agosto, " + "	CONVERT(int,'0') as monto_septiembre, " + "	CONVERT(int,'0') as monto_octubre, " + "	CONVERT(int,'0') as monto_noviembre, " + "	CONVERT(int,'0') as monto_diciembre " + " FROM tProyecto_PF WHERE aEjercicioFiscal =" + ejercicioFiscal + " AND cUnidadNormativa = '" + unidadNormativa + "' ";
        ResultSet rs = null;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            if (capitulosExcluir != null && capitulosExcluir.length > 0)
                for (int i = 0; i < capitulosExcluir.length; i++) query += " AND cPartida NOT LIKE '" + capitulosExcluir[i] + "%' ";
            rs = stmt.executeQuery(query);
            return Util.ExcelFromRS(rs, nombreArchivo);
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(stmt, false);
        }
    }

    /**
     * Inserta un renglon del calendario
     *
     * @param conn
     *            Conexion activa a la base de datos
     * @param infoRenglon
     *            Informacion del renglon
     * @return Numero de registros insertados
     * @throws Exception
     */
    public static int insertaRenglonCalendario(Connection conn, Map<String, String> infoRenglon) throws Exception {
        Statement stmnt = null;
        int r = 0;
        try {
            log.trace("Iniciando insercion de renglon");
            stmnt = conn.createStatement();
            log.debug("Object: {}", Util.genInsertUpdateFromMap("tProyecto_Calendario", infoRenglon, new String[] { "EP" }));
            r = stmnt.executeUpdate(Util.genInsertFromMap("tProyecto_Calendario", infoRenglon));
            log.trace("Object: {}", "Se inserto " + r + "registros");
            return r;
        } finally {
            CloseObject.closeObject(stmnt, false);
        }
    }

    /**
     * Inserta un renglon del proyecto
     *
     * @param conn
     *            Conexion activa a la base de datos
     * @param infoRenglon
     *            Informacion del renglon
     * @return Numero de registros insertados
     * @throws Exception
     */
    public static int insertaRenglonProyecto(Connection conn, Map<String, String> infoRenglon) throws Exception {
        Statement stmnt = null;
        int r = 0;
        try {
            log.trace("Iniciando insercion de renglon");
            stmnt = conn.createStatement();
            r = stmnt.executeUpdate(Util.genInsertFromMap("tProyecto_PF", infoRenglon));
            log.trace("Object: {}", "Se inserto " + r + "registros");
            return r;
        } finally {
            CloseObject.closeObject(stmnt, false);
        }
    }

    /**
     * Inserta la version del proyecto
     *
     * @param conn
     *            Conexion abierta a la base de datos
     * @param ejercicioFiscal
     *            Ejercicio Fiscal
     * @param uLogin
     *            Usuario que genera la version
     * @return Numero de registros insertados
     * @throws Exception
     */
    public static int insertaVersionProyecto(Connection conn, int ejercicioFiscal, String uLogin) throws Exception {
        String query = "INSERT INTO t_control_version_proyecto(nEjercicio_Fiscal, cUsuario_Carga) VALUES(?,?)";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, ejercicioFiscal);
            ps.setString(2, uLogin);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    /**
     * Elimina todo el contenido del proyecto.
     *
     * @param conn
     *            Conexion activa a la base de datos.
     * @return Numero de registros eliminados
     * @throws Exception
     */
    public static int limpiaProyecto(Connection conn) throws Exception {
        String query = "DELETE FROM tProyecto_PF";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }
}
