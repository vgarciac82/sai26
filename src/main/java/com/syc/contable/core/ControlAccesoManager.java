package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ControlAccesoManager {
	public static Map<Integer, String>	casoGrupo	= null;

	public ControlAccesoManager() {
		super();
	}

	public static synchronized void cargaCasoGrupo(Connection conn) throws Exception {

		if (ControlAccesoManager.casoGrupo == null) {
			PreparedStatement pstm = null;
			ResultSet rs = null;
			String sQuery = "select id_tc, g_nombre from tTipoCasoGrupo WITH (NOLOCK)";
			try {
				pstm = conn.prepareStatement(sQuery);
				rs = pstm.executeQuery();
				casoGrupo = new HashMap<Integer, String>();
				while (rs.next()) {
					casoGrupo.put(rs.getInt("id_tc"), rs.getString("g_nombre"));
				}

			} finally {
				CloseObject.closeObject(rs, false);
				CloseObject.closeObject(pstm, false);
			}

		}
	}

	private static String[] getGrupo(int id_tc) {
		if (casoGrupo.get(id_tc) != null)
			return casoGrupo.get(id_tc).split(";");
		else
			return null;
	}

	private static String generaCondicionUsuarioGrupo(int id_tc, String u_login) {
		String[] grupos = getGrupo(id_tc);
		String condicion = "";
		if (grupos != null) {
			condicion = " u_login ='" + u_login + "' AND g_nombre in (";
			String token = "";
			for (int i = 0; i < grupos.length; i++) {
				condicion += token + "'" + grupos[i] + "'" + "," + "'" + "X" + grupos[i] + "X" + "'";
				token = ",";
			}
			condicion += ")";
		}
		return condicion;
	}

	public static ArrayList<String> UnidadesResponsables(Connection conn) throws SQLException {
		ArrayList arrmObtenDatos = new ArrayList();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String cClaveUnidad = "";
		String cDescUnidad = "";
		String sQuery = "SELECT cUnidadResponsable,cUnidadResponsable+' '+D_DESCRIPCION FROM tCatUnidadResponsable with (nolock)";
		try {
			pstm = conn.prepareStatement(sQuery);
			rs = pstm.executeQuery();
			while (rs.next()) {
				ArrayList<String> arrmODatos = new ArrayList<String>();
				cClaveUnidad = rs.getString(1);
				cDescUnidad = rs.getString(2);
				arrmODatos.add(cClaveUnidad);
				arrmODatos.add(cDescUnidad);
				arrmObtenDatos.add(arrmODatos);
				arrmODatos = null;
			}
		} finally {
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return arrmObtenDatos;
	}

	public static List<String> UsuariosCC(Connection conn, String cc, int tc) throws SQLException {
		List<String> arrmObtenDatos = new ArrayList<String>();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sQuery = "";
		String u_login = "";
		String filtrodueno ="('ADMIN')";
		if (tc == -3 || tc == -4)
			filtrodueno ="('ADMIN', 'ADMIN_RECMAT')";
		if (tc != -1 && tc != -2)
			sQuery = "SELECT distinct (tbl.U_LOGIN) FROM ( SELECT ur.U_LOGIN, UR.UP_NOMBRE, UP_VALOR,admin_dueno FROM cg_usuario_propiedades ur WITH (NOLOCK) INNER JOIN cg_usuario u WITH (NOLOCK) ON ur.U_LOGIN = u.U_LOGIN and u.U_ESTATUS = 'A'  and ur.admin_dueno IN " + filtrodueno + " ) AS tbl INNER JOIN cg_usuario_grupo ugrupo WITH (NOLOCK) ON tbl.u_login = ugrupo.u_login WHERE tbl.up_valor = ? AND ( g_nombre IN (SELECT grupo FROM vTIPO_CASO_GRUPOS WITH (NOLOCK) WHERE id_tc = ?) "
					+ " OR  g_nombre IN (SELECT GRUPO_CERRADO FROM vTIPO_CASO_GRUPOS WITH (NOLOCK) WHERE id_tc = "+ tc +")) ";
		else
			sQuery = "SELECT U_LOGIN FROM (SELECT ur.U_LOGIN, UR.UP_NOMBRE, UP_VALOR,admin_dueno FROM cg_usuario_propiedades ur WITH (NOLOCK) INNER JOIN cg_usuario u WITH (NOLOCK) ON ur.U_LOGIN = u.U_LOGIN and u.U_ESTATUS = 'A'  and ur.admin_dueno IN " + filtrodueno + " )  AS tbl where up_valor = ? ";

		try {
			pstm = conn.prepareStatement(sQuery);
			pstm.setString(1, cc);
			if (tc != -1 && tc != -2)
				pstm.setInt(2, tc);
			rs = pstm.executeQuery();
			while (rs.next()) {
				u_login = rs.getString(1);
				arrmObtenDatos.add(u_login);
			}
		} finally {
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return arrmObtenDatos;
	}

	public static boolean apagar(Connection conn, int tc, String ur, String cc, String idUsuario) throws SQLException {
		PreparedStatement pstm = null;
		boolean res = true;
		
		String whereAdecu = "";
		String sQuery = "";
		String where = "";
		String porTramite = "AND id_tc = ?";

		if (tc == -1) {

			if (!ur.equals("-1")) {
				where += " AND ur = ? ";
			}
			if (!cc.equals("-1")) {
				where += " AND centro_contable = ? ";
			}
			if (!idUsuario.equals("")) {
				where += " AND u_login = ? ";
			}
			whereAdecu += " AND U_LOGIN not in (SELECT  U_LOGIN FROM  CG_USUARIO_GRUPO WITH (NOLOCK) WHERE (G_NOMBRE IN ('JEFATURA_ADECUACIONES')))";
			sQuery = "update CG_USUARIO_GRUPO  set g_nombre = 'x' + G_NOMBRE + 'x' where U_LOGIN + G_NOMBRE in ( select u_login + grupo from vimx_usuario_grupo_abierto WITH (NOLOCK) where 1= 1 " + where + ")" + whereAdecu + "";
			pstm = conn.prepareStatement(sQuery);

			if (!ur.equals("-1")) {
				pstm.setString(1, ur);
			}
			if (!cc.equals("-1")) {
				pstm.setString(2, cc);
			}
			if (!idUsuario.equals("")) {
				pstm.setString(3, idUsuario);
			}

			pstm.execute();
			res = pstm.getUpdateCount() != 0 ? true : false;

		} else {

			whereAdecu = "";

			if (!ur.equals("-1")) {
				where += " AND ur = ? ";
			}
			if (!cc.equals("-1")) {
				where += " AND centro_contable = ? ";
			}
			if (!idUsuario.equals("")) {
				where += " AND u_login = ? ";
			}

			if (tc == 3) {
				whereAdecu += " AND U_LOGIN not in (SELECT     U_LOGIN FROM  CG_USUARIO_GRUPO WHERE (G_NOMBRE IN ('JEFATURA_ADECUACIONES')))";
			}
			if (tc == -2) {
				porTramite = " AND id_tc in (4,5,6,7,9,10,11,12,14,18,20,21,23,25,35,43,44,45) ";
			}
			sQuery = "update CG_USUARIO_GRUPO  set g_nombre = 'x' + G_NOMBRE + 'x' where U_LOGIN + G_NOMBRE in ( select u_login + grupo from vimx_usuario_grupo_abierto WITH (NOLOCK) where 1= 1 " + porTramite + where + ")" + whereAdecu + "";
			pstm = conn.prepareStatement(sQuery);

			if (tc != -2) {
				pstm.setInt(1, tc);
			}

			if (!ur.equals("-1")) {
				if (tc == -2) {
					pstm.setString(1, ur);
				} else
					pstm.setString(2, ur);
			}

			if (!cc.equals("-1")) {
				if (tc == -2) {
					pstm.setString(2, cc);
				} else
					pstm.setString(3, cc);
			}
			
			if (!idUsuario.equals("")) {
				if (tc == -2) {
					pstm.setString(3, idUsuario);
				} else
					pstm.setString(4, idUsuario);
			}

			pstm.execute();
			res = pstm.getUpdateCount() != 0 ? true : false;

		}

		ControlAccesoManager.apagarAsuntosAbiertos(conn, tc, ur);

		try {
		} catch (Exception e) {
			res = false;
		}
		return res;
	}

	public static boolean prender(Connection conn, int tc, String ur, String cc, String idUsuario) throws SQLException {
		PreparedStatement pstm = null;
		boolean res = true;
		try {

			String sQuery = "";
			String sQuery1 = "";
			String where = " where 1=1 ";
			String where1 = "";
			String porTramite = "";
			
			if (tc > 0){
					porTramite = "AND id_tc = ?";
				}
			if (tc == -1) {

				if (!ur.equals("-1")) {
					where1 += " AND cUnidadResponsable = ? ";
				}
				if (!cc.equals("-1")) {
					where1 += " AND cCentroContable = ? ";
				}
				if (!idUsuario.equals("")) {
					where1 += " AND usuarios_desactivados = ? ";
				}
				sQuery = "UPDATE CG_USUARIO_GRUPO SET G_NOMBRE = SUBSTRING( g_nombre, 2, len(g_nombre)-2 ) WHERE G_NOMBRE like 'x%x' AND U_LOGIN + g_nombre IN ( select usuarios_desactivados + grupo_cerrado from VIMX_USUARIO_GRUPO_CERRADO WITH (NOLOCK) " + where + where1 + ")";
				pstm = conn.prepareStatement(sQuery);

				if (!ur.equals("-1")) {
					pstm.setString(1, ur);
				}
				if (!cc.equals("-1")) {
					pstm.setString(2, cc);
				}
				if (!idUsuario.equals("")) {
					pstm.setString(3, idUsuario);
				}

				pstm.execute();
				res = pstm.getUpdateCount() != 0 ? true : false;
			} else {

				if (tc == -2) {
					porTramite = " AND id_tc in (4,5,6,7,9,10,11,12,14,18,20,21,23,25,35,43,44,45) ";
				}

				if (tc == -3) {
					porTramite = " AND  grupo_cerrado in ('xRECURSOS_MATERIALES_APTDx') ";
				}

				if (tc == -4) {
					porTramite = " AND  grupo_cerrado in ('xRECURSOS_MATERIALESx') ";
				}

				if (!ur.equals("-1")) {
					where1 += " AND cUnidadResponsable = ? ";
				}
				if (!cc.equals("-1")) {
					where1 += " AND cCentroContable = ? ";
				}
				if (!idUsuario.equals("")) {
					where1 += " AND usuarios_desactivados = ? ";
				}

				sQuery1 = "UPDATE CG_USUARIO_GRUPO SET G_NOMBRE = SUBSTRING( g_nombre, 2, len(g_nombre)-2 ) WHERE G_NOMBRE like 'x%x' AND U_LOGIN + g_nombre IN ( select usuarios_desactivados + grupo_cerrado from VIMX_USUARIO_GRUPO_CERRADO WITH (NOLOCK) " + where + porTramite + where1 + ")";
				pstm = conn.prepareStatement(sQuery1);

				if (tc != -2 && tc != -3 && tc != -4) {
					pstm.setInt(1, tc);
				}
				if (!ur.equals("-1")) {
					if (tc == -2 || tc == -3 || tc == -4) {
						pstm.setString(1, ur);
					} else
						pstm.setString(2, ur);
				}
				if (!cc.equals("-1")) {
					if (tc == -2 || tc == -3 || tc == -4) {
						pstm.setString(2, cc);
					} else
						pstm.setString(3, cc);
				}
				if (!idUsuario.equals("")) {
					if (tc == -2 || tc == -3 || tc == -4) {
						pstm.setString(3, idUsuario);
					} else
						pstm.setString(4, idUsuario);
				}

				pstm.execute();
				res = pstm.getUpdateCount() != 0 ? true : false;

			}

		} catch (Exception e) {
			res = false;
		}
		return res;
	}

	public static void apagarAsuntosAbiertos(Connection conn, int tc, String ur) throws SQLException {
		PreparedStatement pstm = null;
		boolean res = true;

		String sQuery = "UPDATE  cg_caso_operacion SET CO_RESPONSABLE = O.O_RESPONSABLE " + " FROM CG_OPERACION AS O " + " where cg_caso_operacion.ID_OPER = O.ID_OPER " + " AND cg_caso_operacion.ID_TC = O.ID_TC " + " AND CG_CASO_OPERACION.CO_RESPONSABLE NOT LIKE '%CONSULTA%' "
			+ " AND cg_caso_operacion.CO_RESPONSABLE <> O.O_RESPONSABLE";
		if (tc == -2) {
			sQuery += " AND cg_caso_operacion.ID_TC in (4,5,6,7,9,10,11,12,14,18,20,21,23,25,35,43,44,45) ";
		}
		if (tc > 0) {
			sQuery += " AND cg_caso_operacion.ID_TC in (" + tc + ") ";
		}

		pstm = conn.prepareStatement(sQuery);
		try {
			pstm.execute();
			res = pstm.getUpdateCount() != 0 ? true : false;
		} finally {
			if (pstm != null) {
				pstm.close();
			}
		}
	}

	public static boolean validaUsuario(Connection conn, String idUsuario) throws Exception {
		boolean existe = false;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sQuery = "select COUNT (*) from CG_USUARIO_GRUPO WITH (NOLOCK) where U_LOGIN = ?";
		try {
			pstm = conn.prepareStatement(sQuery);
			pstm.setString(1, idUsuario);
			rs = pstm.executeQuery();

			if (rs.next())
				existe = rs.getInt(1) > 0;
			return existe;
		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(pstm, false);
		}

	}

	public static boolean validaUsuarioGrupo(Connection conn, String idUsuario, int tc) throws Exception {
		boolean existe = false;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sQuery = "select COUNT (*) from CG_USUARIO_GRUPO WITH (NOLOCK) where ";
		try {
			cargaCasoGrupo(conn);
			String cond = ControlAccesoManager.generaCondicionUsuarioGrupo(tc, idUsuario);
			if (cond == null || "".equals(cond)) {
				throw new Exception("No existen grupos definidos para el tipo de tramite " + tc + " en la tabla tTipoCasoGrupo");
			}
			sQuery += cond;
			pstm = conn.prepareStatement(sQuery);
			rs = pstm.executeQuery();
			if (rs.next())
				existe = rs.getInt(1) > 0;
			return existe;
		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(pstm, false);
		}

	}

}
