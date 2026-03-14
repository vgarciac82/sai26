package com.axtel.presupuesto;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import com.axtel.ws.clients.AdecuacionRespuesta;
import com.syc.contable.adecuaciones.UsuarioNotificado;
import com.syc.contable.adecuaciones.UsuarioSiplan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdecuacionRespuestaDAO {

    private static final QueryRunner runner = new QueryRunner();

    private static final StringBuilder queryInsert = new StringBuilder("INSERT INTO  tUsuarioAdecuacionNotificado(nFolioAdecuacion,cUsuarioNotificado,cUsuarioCorreo )VALUES(? ,?, ? )");

    private static final StringBuilder queryInsertDet = new StringBuilder("INSERT INTO  tUsuarioAdecuacionNotificadoPrograma(nFolioAdecuacion, cUsuarioNotificado, cOrden )VALUES(? ,?, ? )");

    private static final Logger log = LoggerFactory.getLogger(AdecuacionRespuestaDAO.class);

    public static boolean existeNoficado(Connection conn, int folioAdecuacion, String usuario) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) AS existe from tUsuarioAdecuacionNotificado WHERE nFolioAdecuacion = ? AND cUsuarioNotificado = ?");
        ScalarHandler<Integer> kh = new ScalarHandler<Integer>("existe");
        return runner.query(conn, query.toString(), kh, folioAdecuacion, usuario) > 0;
    }

    public static boolean existeDetalleNotificacion(Connection conn, int folioAdecuacion, String usuario, String orden) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	COUNT(*) AS existe  ");
        query.append("  FROM	tUsuarioAdecuacionNotificadoPrograma  WITH(nolock) ");
        query.append(" WHERE	nFolioAdecuacion = ? ");
        query.append("   AND	cUsuarioNotificado = ? ");
        query.append("   AND	corden = ? ");
        ScalarHandler<Integer> kh = new ScalarHandler<Integer>("existe");
        return runner.query(conn, query.toString(), kh, folioAdecuacion, usuario, orden) > 0;
    }

    public static int insertaRespuesta(Connection conn, int folioAdecuacion, AdecuacionRespuesta respuesta) throws SQLException {
        log.info("Object: {}", "Insertando objeto: " + respuesta);
        int insertados = 0;
        for (UsuarioNotificado usuario : respuesta.getUsuariosNotificar()) {
            if (!existeNoficado(conn, folioAdecuacion, usuario.getUsuario())) {
                insertados += runner.update(conn, queryInsert.toString(), folioAdecuacion, usuario.getUsuario(), usuario.getCorreo());
                for (String orden : usuario.getnOrden()) {
                    if (!existeDetalleNotificacion(conn, folioAdecuacion, usuario.getUsuario(), orden))
                        insertados += runner.update(conn, queryInsertDet.toString(), folioAdecuacion, usuario.getUsuario(), orden);
                }
            }
        }
        log.trace("Object: {}", "Se insertaron :  " + insertados + " Regustros en tUsuarioAdecuacionNotificado");
        return insertados;
    }

    public static List<UsuarioNotificado> leeUsuariosNotificados(Connection conn, int folioAdecuacion) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT uNotificado.nfolioadecuacion            AS folio, ");
        query.append("       uNotificado.cusuarionotificado          AS loginUsuario, ");
        query.append("       nombren + ' ' + nombrep + ' ' + nombrem AS nombre, ");
        query.append("       descripcion_puesto                      AS puesto, ");
        query.append("       unidad                                  AS unidad, ");
        query.append("       ISNULL( d_email, cUsuarioCorreo)        AS correo ");
        query.append("FROM   tusuarioadecuacionnotificado AS uNotificado ");
        query.append("       LEFT OUTER JOIN v_empleados_giro empleado ");
        query.append("                    ON Replace(empleado.d_email, '@conafor.gob.mx', '') = ");
        query.append("                       uNotificado.cusuarionotificado  ");
        query.append("WHERE   uNotificado.nFolioAdecuacion = ? ");
        return leeUsuarios(conn, folioAdecuacion, query.toString());
    }

    private static List<UsuarioNotificado> leeUsuarios(Connection conn, int folioAdecuacion, String query) throws SQLException {
        ResultSetHandler<List<UsuarioNotificado>> h = new BeanListHandler<UsuarioNotificado>(UsuarioNotificado.class);
        List<UsuarioNotificado> usuarios = runner.query(conn, query, h, folioAdecuacion);
        return usuarios;
    }

    private static List<UsuarioSiplan> leeUsuariosSIPLAN(Connection conn, int folioAdecuacion, String query) throws SQLException {
        ResultSetHandler<List<UsuarioSiplan>> h = new BeanListHandler<UsuarioSiplan>(UsuarioSiplan.class);
        List<UsuarioSiplan> usuarios = runner.query(conn, query, h, folioAdecuacion);
        return usuarios;
    }

    public static List<UsuarioSiplan> leeUsuariosCapturistas(Connection conn, int folioAdecuacion) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	").append(folioAdecuacion).append(" AS folio, ");
        query.append("		u.U_LOGIN AS loginUsuario, ");
        query.append("		u.U_NOMBRE AS nombre, ");
        query.append("		e.CARGO AS puesto, ");
        query.append("		ur.cUnidadResponsable AS unidad,  ");
        query.append("		u.U_EMAIL AS correo ");
        query.append("FROM   cg_usuario u ");
        query.append("       INNER JOIN cg_cat_empleado e ");
        query.append("               ON u.u_login = e.ce_os_responsable ");
        query.append("       INNER JOIN tcatalogounidadresponsable ur ");
        query.append("               ON e.id_area = ur.id_area ");
        query.append("       INNER JOIN cg_usuario_grupo ug ");
        query.append("               ON u.u_login = ug.u_login ");
        query.append("WHERE  u.u_estatus = 'A' ");
        query.append("       AND ur.cunidadresponsable IN (SELECT DISTINCT Substring(ep, 57, 3) AS ");
        query.append("                                                     unidades ");
        query.append("                                     	FROM	tadecuaciondetalle detalle ");
        query.append("												INNER JOIN ");
        query.append("											tCatalogoMetas metas  ");
        query.append("											ON  ");
        query.append("											SUBSTRING(detalle.ep, 45,11) = metas.cCartera ");
        query.append("                                     WHERE  nfolioadecuacion = ?) ");
        query.append("       AND g_nombre LIKE '%SOLICITANTES_ADECUACIONES%'  ");
        return leeUsuariosSIPLAN(conn, folioAdecuacion, query.toString());
    }
}
