package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.PaginaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeguimientoManager {

    private static Logger log = LoggerFactory.getLogger(SeguimientoManager.class);

    public static PaginaData select(Connection conn, Seguimiento seg, String u_login, PaginaData param_pd) throws SQLException {
        PaginaData pd = new PaginaData();
        List listSeg = new ArrayList();
        PreparedStatement pstmnt = null;
        //PreparedStatement pstmnt1 = null;
        ResultSet rs = null;
        int TotalRegistros = -1;
        //Queda pendiente la busqueda por folio
        //String folio = param_pd.getBuscarFiltro();
        //boolean hayFolio = folio != null && !"".equals(folio);
        //Cobertura c = null;
        //String whereCBO;
        //String fromCBO;
        String respuesta;
        try {
            // 1. Genera condicion para consulta a BD (a partir de Lista de campos y valores del criterio de seleccion)
            String token = " AND ";
            StringBuffer where = new StringBuffer();
            /*		if (seg.getIdTC() > -1) {
				where.append(token + "tc.id_tc = ?");
				token = " AND ";
			}
	*/
            if (seg.getIdCaso() > -1) {
                where.append(token + "v.id_caso = ?");
                token = " AND ";
            }
            if (seg.getPadre() > -1) {
                where.append(token + "b.b_id_caso_oper = ?");
                token = " AND ";
            }
            if (seg.getHijo() > -1) {
                where.append(token + "b.b_co_id_caso_oper_sigte = ?");
                token = " AND ";
            }
            if (seg.getFolio() != null) {
                where.append(token + "b.b_c_folio = ?");
                token = " AND ";
            }
            if (seg.getGavetaAsociada() != null) {
                where.append(token + "tc.tc_gaveta_asociada = ?");
                token = " AND ";
            }
            if (seg.getIdGabinete() > -1) {
                where.append(token + "b.b_c_id_gabinete = ?");
                token = " AND ";
            }
            if (seg.getFechaCreacion() != null) {
                where.append(token + "b.b_c_fecha_ini = ?");
                token = " AND ";
            }
            if (seg.getFechaEnvio() != null) {
                where.append(token + "b.b_co_fecha_ini = ?");
                token = " AND ";
            }
            if (seg.getDescripcion() != null) {
                where.append(token + "tc.tc_descripcion = ?");
                token = " AND ";
            }
            if (seg.getOperacionEjec() != null) {
                where.append(token + "o.o_nombre = ?");
                token = " AND ";
            }
            if (seg.getResponsableEjec() != null) {
                where.append(token + "b.b_co_responsable_ejec = ?");
                token = " AND ";
            }
            if (seg.getOperacionSigte() != null) {
                where.append(token + "b.b_co_operacion_sigte = ?");
                token = " AND ";
            }
            if (seg.getResponsableSigte() != null) {
                where.append(token + " b.b_co_responsable_sigte = ?");
                token = " AND ";
            }
            if (seg.getReferencia() != null) {
                where.append(token + " imx.referencia = ?");
                token = " AND ";
            }
            if (seg.getAsunto() != null) {
                where.append(token + " imx.asunto = ?");
                token = " AND ";
            }
            // 2. Agrega cobertura operativa (si procede)
            //if (u_login == null) {
            //	c = null;
            //} else {
            //	String titulo_aplicacion = ""; // La consulta de seguimiento no se restringe a un Gabinete en particular
            //	c = CoberturaManager.selectByIdUsuario(conn, u_login, GestionInterface.CBO_CAT_AREAS, GestionInterface.CBO_PRD_GESTION, titulo_aplicacion);
            //}
            //if (c != null) {
            //	whereCBO = c.getCoWhereClause();
            //	fromCBO = c.getCoFromClause();
            //} else {
            //	whereCBO = "";
            //	fromCBO = "";
            //}
            // String query = "SELECT tc.id_tc, b.b_id_caso, b_id_caso_oper,
            // b.b_co_id_caso_oper_sigte, b.b_c_folio, " +
            // "tc.tc_gaveta_asociada, b.b_c_id_gabinete, b.b_c_fecha_ini,
            // b.b_co_fecha_ini, tc.tc_descripcion, " + "o.o_nombre,
            // b.b_co_responsable_ejec, b.b_co_operacion_sigte,
            // b.b_co_responsable_sigte, b.b_co_observacion, " + "CASE WHEN
            // co.id_caso IS NULL THEN 'cerrado' ELSE 'activo' END AS estado " +
            // "FROM cg_tipo_caso tc, cg_operacion o, cg_caso_operacion co,
            // cg_bitacora b " + "WHERE tc.id_tc = b.b_id_tc " + "AND o.id_tc =
            // b.b_id_tc AND o.id_oper = b.b_id_oper + "AND co.id_tc(+) =
            // b.b_id_tc AND co.id_caso(+) = b.b_id_caso AND co.id_caso_oper(+)
            // =
            // b.b_co_id_caso_oper_sigte " + where + " ORDER BY b.b_id_tc,
            // b.b_id_caso, b.id_bitacora";
            // String query = "SELECT tc.id_tc, b.b_id_caso, b_id_caso_oper,
            // b.b_co_id_caso_oper_sigte, b.b_c_folio, "
            // + "tc.tc_gaveta_asociada, b.b_c_id_gabinete, b.b_c_fecha_ini,
            // b.b_co_fecha_ini, tc.tc_descripcion, "
            // + "o.o_nombre, b.b_co_responsable_ejec, b.b_co_operacion_sigte, "
            // + "b.b_co_responsable_sigte, b.b_co_observacion, "
            // + "CASE WHEN co.id_caso IS NULL THEN 'cerrado' ELSE 'activo' END
            // AS estado, "
            // + "co.user08 AS respuesta " // Adicionado para CNA
            // + "FROM cg_tipo_caso tc, cg_operacion o, "
            // + "cg_bitacora b LEFT OUTER JOIN cg_caso_operacion co ON
            // (co.id_tc = "
            // + "b.b_id_tc AND co.id_caso = b.b_id_caso AND co.id_caso_oper =
            // b.b_co_id_caso_oper_sigte)"
            // + "WHERE tc.id_tc = b.b_id_tc AND o.id_tc = "
            // + "b.b_id_tc AND o.id_oper = b.b_id_oper"
            // + where
            // + " ORDER BY b.b_id_tc, b.b_id_caso, b.id_bitacora";
            // Supone que la COBERTURA siempre usa la tabla "caso_operacion co"
            // (verificar en com.syc.gestion.core.CoberturaManager.java)
            // Obteniendo el total de registros para aplicar paginacion
            /*
			String query = 
			      "SELECT Count(tc.id_tc)" 
				+ "FROM cg_tipo_caso tc" 
				+ ", cg_operacion o" 
				+ ", cg_bitacora b" 
				+ ", cg_bitacora_caso bc" 
				+ ", cg_bitacora_operacion bo " 
				+ ("".equals(fromCBO) ? ", cg_caso_operacion co" : "," + fromCBO) + " " 
				+ "WHERE tc.id_tc = b.b_id_tc "
				+ "AND o.id_tc = b.b_id_tc " 
				+ "AND o.id_oper = b.b_id_oper "
				+ "AND co.id_tc = b.b_id_tc " 
				+ "AND co.id_caso = b.b_id_caso " 
				+ "AND co.id_caso_oper = b.b_co_id_caso_oper_sigte "
				+ "AND co.id_caso = bc.id_caso " 
				+ "AND co.id_caso = bo.id_caso " 
				+ "AND co.id_caso_oper = bo.secuencial_operacion " 
				+ where
				+ ("".equals(whereCBO) ? "" : " " + whereCBO) 
				+ " ORDER BY b.b_id_tc" 
				+ ", b.b_id_caso" 
				+ ", b.id_bitacora";


			//log.debug("query=[" + query + "] where=[" + where + "]");
			pstmnt1 = conn.prepareStatement(query);

			int idx = 1;

			if (seg.getIdTC() > -1)
				pstmnt.setInt(idx++, seg.getIdTC());

			if (seg.getIdCaso() > -1)
				pstmnt.setInt(idx++, seg.getIdCaso());

			if (seg.getPadre() > -1)
				pstmnt.setInt(idx++, seg.getPadre());

			if (seg.getHijo() > -1)
				pstmnt.setInt(idx++, seg.getHijo());

			if (seg.getFolio() != null)
				pstmnt.setString(idx++, seg.getFolio());

			if (seg.getGavetaAsociada() != null)
				pstmnt.setString(idx++, seg.getGavetaAsociada());

			if (seg.getIdGabinete() > -1)
				pstmnt.setInt(idx++, seg.getIdGabinete());

			if (seg.getFechaCreacion() != null)
				pstmnt.setTimestamp(idx++, seg.getFechaCreacion());

			if (seg.getFechaEnvio() != null)
				pstmnt.setTimestamp(idx++, seg.getFechaEnvio());

			if (seg.getDescripcion() != null)
				pstmnt.setString(idx++, seg.getDescripcion());

			if (seg.getOperacionEjec() != null)
				pstmnt.setString(idx++, seg.getOperacionEjec());

			if (seg.getResponsableEjec() != null)
				pstmnt.setString(idx++, seg.getResponsableEjec());

			if (seg.getOperacionSigte() != null)
				pstmnt.setString(idx++, seg.getOperacionSigte());

			if (seg.getResponsableSigte() != null)
				pstmnt.setString(idx++, seg.getResponsableSigte());

			rs = pstmnt1.executeQuery();
			
			if(rs.next())
				TotalRegistros = rs.getInt(1);
			//fin total de registros
			*/
            //Ricardo: Le agrege lo de fecha inicio
            String query = "SELECT v.FECHA_ENVIO,v.id_caso " + ",v.folio " + ",id_tc " + ",id_operacion " + ",id_caso_oper " + ",secuencial_operacion" + ",secuencial_anterior " + ",id_gabinete " + ",fecharegistro " + ",fecha_compromiso " + ",fechaLimite " + ",user03 " + ",user05 " + ",replace(replace(user08,char(13),' '),char(10),' ') as user08 " + ",replace(replace(user10,char(13),' '),char(10),' ') as user10 " + ",responsable_asunto " + ",nombre_resp " + ",cerrado " + ",terminada " + ",replace(replace(asunto,char(13),' '),char(10),' ') as asunto " + ",referencia " + ",remitente_asunto " + ",cargo " + ",nombre_rem " + ",cargo_rem " + ",responsable_asunto " + ",puesto_responsable_asunto " + ",remitente_id " + ",otroresponsable as responsable_id " + ",tipo_instruccion " + "FROM vimx_seguimiento v " + "WHERE 1=1 " + where + " " + "ORDER BY v.secuencial_operacion";
            /*
			String query_pag = " SELECT * from ( " + query + "     ) as seg "
							+ "  WHERE RowNumber BETWEEN " + (param_pd.getTamanoPaginas() * param_pd.getNumeroPagina() + 1) 
		    				+ " AND " + param_pd.getTamanoPaginas() * (param_pd.getNumeroPagina() + 1);
			*/
            System.out.println("seguimiento arbol: " + query);
            //log.debug("query=[" + query + "] where=[" + where + "]");
            pstmnt = conn.prepareStatement(query);
            int idx = 1;
            /*		if (seg.getIdTC() > -1)
				pstmnt.setInt(idx++, seg.getIdTC());
    */
            if (seg.getIdCaso() > -1)
                pstmnt.setInt(idx++, seg.getIdCaso());
            if (seg.getPadre() > -1)
                pstmnt.setInt(idx++, seg.getPadre());
            if (seg.getHijo() > -1)
                pstmnt.setInt(idx++, seg.getHijo());
            if (seg.getFolio() != null)
                pstmnt.setString(idx++, seg.getFolio());
            if (seg.getGavetaAsociada() != null)
                pstmnt.setString(idx++, seg.getGavetaAsociada());
            if (seg.getIdGabinete() > -1)
                pstmnt.setInt(idx++, seg.getIdGabinete());
            if (seg.getFechaCreacion() != null)
                pstmnt.setTimestamp(idx++, seg.getFechaCreacion());
            if (seg.getFechaEnvio() != null)
                pstmnt.setTimestamp(idx++, seg.getFechaEnvio());
            if (seg.getDescripcion() != null)
                pstmnt.setString(idx++, seg.getDescripcion());
            if (seg.getOperacionEjec() != null)
                pstmnt.setString(idx++, seg.getOperacionEjec());
            if (seg.getResponsableEjec() != null)
                pstmnt.setString(idx++, seg.getResponsableEjec());
            if (seg.getOperacionSigte() != null)
                pstmnt.setString(idx++, seg.getOperacionSigte());
            if (seg.getResponsableSigte() != null)
                pstmnt.setString(idx++, seg.getResponsableSigte());
            if (seg.getReferencia() != null)
                pstmnt.setString(idx++, seg.getReferencia());
            if (seg.getAsunto() != null)
                pstmnt.setString(idx++, seg.getAsunto());
            /*			
			if (seg.getRegistro() != null)
				pstmnt.setString(idx++, seg.getRegistro());
			
			if (seg.getResponsable() != null)
				pstmnt.setString(idx++, seg.getResponsable());
			
			if (seg.getResponsable() != null)
				pstmnt.setString(idx++, seg.getResponsableCargo());				
*/
            respuesta = selectRespuesta(conn, seg.getIdCaso());
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Seguimiento s = new Seguimiento();
                s.setIdTC(rs.getInt("id_tc"));
                s.setIdOperacion(rs.getInt("id_operacion"));
                s.setIdCasoOperacion(rs.getInt("id_caso_oper"));
                s.setSecuencialOperacion(rs.getInt("secuencial_operacion"));
                s.setSecuencialAnterior(rs.getInt("secuencial_anterior"));
                s.setIdCaso(rs.getInt("id_caso"));
                s.setFolio(rs.getString("folio"));
                s.setPadre(rs.getInt("id_operacion"));
                s.setHijo(rs.getInt("secuencial_anterior"));
                //		s.setGavetaAsociada(rs.getString("tc_gaveta_asociada"));
                s.setIdGabinete(rs.getInt("id_gabinete"));
                s.setFechaCreacion(rs.getTimestamp("fecharegistro"));
                //Ricardo: Fecha envio estaban llenandola con fecha compromiso
                s.setFechaEnvio(rs.getTimestamp("FECHA_ENVIO"));
                s.setFechaLimite(rs.getTimestamp("fechalimite"));
                s.setFechaOtorgada(rs.getString("user05"));
                s.setFechaRecepcion(rs.getString("user03"));
                //		s.setDescripcion(rs.getString("tc_descripcion"));
                //		s.setOperacionEjec(rs.getString("o_nombre"));
                s.setResponsableEjec(rs.getString("responsable_asunto"));
                s.setResponsableOperacion(rs.getString("nombre_resp"));
                //		s.setOperacionSigte(rs.getString("b_co_operacion_sigte"));
                //		s.setResponsableSigte(rs.getString("b_co_responsable_sigte"));
                //		s.setObservacion(rs.getString("b_co_observacion"));
                s.setEstado(rs.getString("terminada"));
                s.setEstadoAsunto(rs.getString("cerrado"));
                s.setAsunto(rs.getString("asunto"));
                //RDMB: SE manda tipo de instruccion al set
                s.setTipoInstruccion(rs.getString("tipo_instruccion"));
                s.setRespuestaAsunto(respuesta);
                s.setRespuestaParcial(rs.getString("user08"));
                s.setRechazoRespuesta(rs.getString("user10"));
                s.setReferencia(rs.getString("referencia"));
                s.setRemitente(rs.getString("remitente_asunto"));
                s.setRemitenteCargo(rs.getString("cargo"));
                s.setRegistro(rs.getString("nombre_rem"));
                s.setRegistroCargo(rs.getString("cargo_rem"));
                s.setResponsable(rs.getString("responsable_asunto"));
                s.setResponsable(rs.getString("responsable_id"));
                s.setResponsableCargo(rs.getString("puesto_responsable_asunto"));
                s.setIdRemitente(rs.getString("remitente_id"));
                // Adicionado para CNA
                //				s.setRespuesta(rs.getString("respuesta"));
                // Fin adicionado para CNA
                listSeg.add(s);
            }
            pd.setTamanoPaginas(param_pd.getTamanoPaginas());
            pd.setNumeroRegistros(TotalRegistros);
            pd.setParamConsulta(param_pd.getParamConsulta());
            pd.setBuscarFiltro(param_pd.getBuscarFiltro());
            pd.setLista(listSeg);
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return pd;
    }

    public static String selectRespuesta(Connection conn, int id_caso) throws SQLException {
        //PaginaData pd = new PaginaData();
        //List listSeg = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        StringBuffer where = new StringBuffer();
        String respuesta = "";
        try {
            if (id_caso > 0) {
                where.append("v.id_caso = ?");
            }
            String query = "SELECT v.user08 " + "FROM vimx_reportes v " + "WHERE  " + where + " " + "AND id_operacion = 5 " + "ORDER BY v.secuencial_operacion";
            pstmnt = conn.prepareStatement(query);
            int idx = 1;
            if (id_caso > -1)
                pstmnt.setInt(idx++, id_caso);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                respuesta = rs.getString("user08");
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return respuesta;
    }

    public static PaginaData selectAgrupado(Connection conn, SeguimientoConsulta sc, String u_login, PaginaData in_pd) throws SQLException {
        PaginaData out_pd = new PaginaData();
        List listSeg = new ArrayList();
        PreparedStatement pstmnt = null;
        //PreparedStatement pstmnt1 = null;
        ResultSet rs = null;
        int TotalRegistros = -1;
        //Queda pendiente la busqueda por folio
        //String folio = in_pd.getBuscarFiltro();
        //boolean hayFolio = folio != null && !"".equals(folio);
        Cobertura c = null;
        String whereCBO;
        String fromCBO;
        try {
            // 1. Genera condicion para consulta a BD (a partir de Lista de campos y valores del criterio de seleccion)
            String token = " AND ";
            StringBuffer where = new StringBuffer();
            if (sc.getIdTC() > -1) {
                where.append(token + " s.tc_id_tc = ?");
                token = " AND ";
            }
            if (sc.getIdOper() > -1) {
                where.append(token + " s.b_id_oper = ?");
                token = " AND ";
            }
            if (sc.getIdCaso() > -1) {
                where.append(token + " s.b_id_caso = ?");
                token = " AND ";
            }
            if ((sc.getFolioIni() != null) && (sc.getFolioEnd() != null)) {
                where.append(token + " s.b_c_folio BETWEEN '" + sc.getFolioIni() + "' AND '" + sc.getFolioEnd() + "'");
                token = " AND ";
            } else if (sc.getFolioIni() != null) {
                where.append(token + " s.b_c_folio like '%" + sc.getFolioIni() + "%'");
                token = " AND ";
            } else if (sc.getFolioEnd() != null) {
                where.append(token + " s.b_c_folio like '%" + sc.getFolioEnd() + "%'");
                token = " AND ";
            }
            /*	if (sc.getFolioIni() != null) {
				where.append(token + " s.b_c_folio like '%" + sc.getFolioIni() +"%'");
				token = " AND ";
			}	
		*/
            if ((sc.getFechaCreacionIni() != null) && ((sc.getFechaCreacionEnd() != null))) {
                where.append(token + " s.b_c_fecha_ini BETWEEN CONVERT(datetime, '" + sc.getFechaCreacionIni() + "', 103) AND CONVERT(datetime, '" + sc.getFechaCreacionEnd() + "' , 103)");
                //where.append(token + " s.b_c_fecha_ini BETWEEN ? AND ? ");
                token = " AND ";
            } else if (sc.getFechaCreacionIni() != null) {
                where.append(token + " s.b_c_fecha_ini = CONVERT(datetime, '" + sc.getFechaCreacionIni() + "', 103)");
                token = " AND ";
            } else if (sc.getFechaCreacionEnd() != null) {
                where.append(token + " s.b_c_fecha_ini = CONVERT(datetime, '" + sc.getFechaCreacionEnd() + "' , 103)");
                token = " AND ";
            }
            /*		if ((sc.getFechaEnvioIni() != null) && ((sc.getFechaEnvioEnd() != null))) {
				where.append(token + " s.b_co_fecha_ini BETWEEN CONVERT(datetime, '" + sc.getFechaEnvioIni() + "', 103) AND CONVERT(datetime, '" + sc.getFechaEnvioEnd() + "' , 103)");
				token = " AND ";
			} else if (sc.getFechaEnvioIni() != null) {
				where.append(token + " s.b_co_fecha_ini = CONVERT(datetime, '" + sc.getFechaEnvioIni() + "', 103)");
				token = " AND ";
			} else if (sc.getFechaEnvioEnd() != null) {
				where.append(token + " s.b_co_fecha_ini = CONVERT(datetime, '" + sc.getFechaEnvioEnd() + "' , 103)");
				token = " AND ";
			}
	
			if (sc.getFechaEnvioIni() != null) {
				where.append(token + " s.b_co_fecha_ini = CONVERT(datetime, '" + sc.getFechaEnvioIni() + "', 103)");
				token = " AND ";
			}
	*/
            if (sc.getDescripcion() != null) {
                where.append(token + " s.tc_descripcion = ?");
                token = " AND ";
            }
            /*Ricardo: Se comenta esta linea para ya no ligar contra la tabla de cg_bitacora
			if (sc.getResponsableEjec() != null) {
				where.append(token + " s.b_co_responsable_ejec = ?");
				token = " AND ";
			}
            */
            if (sc.getResponsableSigte() != null) {
                where.append(token + " s.b_co_responsable_sigte = ?");
                token = " AND ";
            }
            if (sc.getReferencia() != null) {
                where.append(token + " imx.referencia = ?");
                token = " AND ";
            }
            if (sc.getAsunto() != null) {
                where.append(token + " imx.asunto like '%" + sc.getAsunto() + "%'");
                token = " AND ";
            }
            // 2. Agrega cobertura operativa (si procede)
            if (u_login == null) {
                c = null;
            } else {
                // La consulta de seguimiento no se restringe a un Gabinete en particular
                String titulo_aplicacion = "";
                c = CoberturaManager.selectByIdUsuario(conn, u_login, GestionInterface.CBO_CAT_AREAS, GestionInterface.CBO_PRD_GESTION, titulo_aplicacion);
            }
            if (c != null) {
                whereCBO = c.getCoWhereClause();
                fromCBO = c.getCoFromClause();
            } else {
                whereCBO = "";
                fromCBO = "";
            }
            /*
			 * String query = "SELECT tc.id_tc, b.b_id_caso, b.b_c_folio, " +
			 * "MIN(b.b_c_fecha_ini) AS b_c_fecha_ini, " +
			 * "MAX(b.b_co_fecha_ini) AS b_co_fecha_ini, " +
			 * "MAX(tc.tc_descripcion) AS tc_descripcion, " + "MIN(CASE WHEN
			 * co.id_caso IS NULL THEN 'cerrado' ELSE 'activo' END) AS estado " +
			 * "FROM cg_tipo_caso tc, cg_operacion o, cg_caso_operacion co,
			 * cg_bitacora b " + "WHERE tc.id_tc = b.b_id_tc AND o.id_tc =
			 * b.b_id_tc AND o.id_oper = b.b_id_oper " + "AND co.id_tc(+) =
			 * b.b_id_tc AND co.id_caso(+) = b.b_id_caso " + "AND
			 * co.id_caso_oper(+) = b.b_co_id_caso_oper_sigte " + where + "
			 * GROUP BY tc.id_tc, b.b_id_caso, b.b_c_folio ORDER BY 1,2,3";
			 */
            /*
			
			String query =
			      "SELECT DISTINCT tc.id_tc"
				+ ",     b.b_id_caso" 
				+ ",     b.b_c_folio" 
				+ ",     MIN(b.b_c_fecha_ini)   AS b_c_fecha_ini" 
				+ ",     MAX(b.b_co_fecha_ini)  AS b_co_fecha_ini" 
				+ ",     tc.tc_descripcion AS tc_descripcion" 
				+ ",     MIN(CASE WHEN bc.cerrado = 'S' THEN 'cerrado' ELSE 'activo' END) AS estado "
				+ "FROM  cg_tipo_caso tc" 
				+ ",     cg_operacion o" 
				+ ",     cg_caso_operacion co1" 
				+ ",     cg_bitacora b" 
				+ ",     cg_bitacora_caso bc" 
				+ ",     cg_bitacora_operacion bo " 
				+ ("".equals(fromCBO) ? "" : "," + fromCBO) + " " 
				+ "WHERE tc.id_tc         = b.b_id_tc "
				+ "AND   o.id_tc          = b.b_id_tc " 
				+ "AND   o.id_oper        = b.b_id_oper "
				+ "AND   co1.id_tc        = b.b_id_tc " 
				+ "AND   co1.id_caso      = b.b_id_caso " 
				+ "AND   co1.id_caso_oper = b.b_co_id_caso_oper_sigte "
				+ "AND   co1.id_caso      = bc.id_caso " 
				+ "AND   co1.id_caso      = bo.id_caso " 
				+ "AND   co1.id_caso_oper = bo.secuencial_operacion  " 
				+ where
				+ ("".equals(whereCBO) ? "" : " " + whereCBO) 
				+ " GROUP BY tc.id_tc, b.b_id_caso, b.b_c_folio, tc.tc_descripcion "; 
				//+ "ORDER BY 1,2,3";
			*/
            String query = //, "
            "SELECT Count(*) " + "     FROM ( " + "          SELECT s.tc_id_tc, " + "                 s.b_id_caso, " + "                 s.b_c_folio, " + "                 MIN(s.b_c_fecha_ini)   AS b_c_fecha_ini, " + "	                MAX(s.b_co_fecha_ini)  AS b_co_fecha_ini, " + //+"					imx.asunto,"
            "                 MIN(CASE WHEN s.bc_cerrado = 'S' THEN 'cerrado' ELSE 'activo' END) AS estado " + //+"					imx.referencia"
            "            FROM fx_seguimientoConsulta( ?, '') as s" + "            " + ("".equals(fromCBO) ? "" : "," + fromCBO) + " ,imxanp imx" + "           WHERE " + "                s.c_id_caso = c.id_caso " + "				AND imx.id_gabinete = c.c_id_gabinete " + "             AND e.ce_os_responsable = co.co_responsable" + "             AND a.id_area           = e.id_area" + "           " + ("".equals(whereCBO) ? "" : " " + whereCBO) + //,imx.asunto,imx.referencia "
            where + "          GROUP BY s.tc_id_tc, s.b_id_caso, s.b_c_folio " + "          ) as seg";
            //log.info("query=[" + query + "]");
            //query = "SELECT Count(*) FROM ( "
            //	+ query + " ) as seg ";
            pstmnt = conn.prepareStatement(query);
            int idx = 1;
            if (sc.getIdTC() > -1) {
                //log.debug("parameter" + idx + "=[" + sc.getIdTC() + "]");
                pstmnt.setInt(idx++, sc.getIdTC());
            }
            if (sc.getIdOper() > -1) {
                //log.debug("parameter" + idx + "=[" + sc.getIdOper() + "]");
                pstmnt.setInt(idx++, sc.getIdOper());
            }
            if (sc.getIdCaso() > -1) {
                //log.debug("parameter" + idx + "=[" + sc.getIdCaso() + "]");
                pstmnt.setInt(idx++, sc.getIdCaso());
            }
            if ((sc.getFolioIni() != null) && (sc.getFolioEnd() != null)) {
                //log.debug("parameter" + idx + "=[" + sc.getFolioIni() + "]");
                //pstmnt.setString(idx++, sc.getFolioIni());
                //log.debug("parameter" + idx + "=[" + sc.getFolioEnd() + "]");
                //pstmnt.setString(idx++, sc.getFolioEnd());
            } else if (sc.getFolioIni() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getFolioIni() + "]");
                //pstmnt.setString(idx++, sc.getFolioIni());
            } else if (sc.getFolioEnd() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getFolioEnd() + "]");
                //pstmnt.setString(idx++, sc.getFolioEnd());
            }
            if ((sc.getFechaCreacionIni() != null) && (sc.getFechaCreacionEnd() != null)) {
                //log.debug("parameter" + idx + "=[" + sc.getFechaCreacionIni() + "]");
                //pstmnt.setTimestamp(idx++, sc.getFechaCreacionIni());
                ////pstmnt.setString(idx++, "Convert(datetime, " + sc.getFechaEnvioIni() + ", 103)");
                //log.debug("parameter" + idx + "=[" + sc.getFechaEnvioEnd() + "]");
                //pstmnt.setTimestamp(idx++, sc.getFechaCreacionEnd());
                ////pstmnt.setString(idx++, "Convert(datetime, " + sc.getFechaEnvioEnd() + ", 103)");
            } else if (sc.getFechaCreacionIni() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getFechaCreacionIni() + "]");
                //pstmnt.setString(idx++, sc.getFechaCreacionIni());
            } else if (sc.getFechaCreacionEnd() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getFechaCreacionEnd() + "]");
                //pstmnt.setString(idx++, sc.getFechaCreacionEnd());
            }
            if ((sc.getFechaEnvioIni() != null) && (sc.getFechaEnvioEnd() != null)) {
                ////log.debug("parameter" + idx + "=[" + sc.getFechaEnvioIni() + "]");
                //pstmnt.setTimestamp(idx++, sc.getFechaEnvioIni());
                //log.debug("parameter" + idx + "=[" + sc.getFechaEnvioEnd() + "]");
                //pstmnt.setTimestamp(idx++, sc.getFechaEnvioEnd());
            } else if (sc.getFechaEnvioIni() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getFechaEnvioIni() + "]");
                //pstmnt.setString(idx++, sc.getFechaEnvioIni());
            } else if (sc.getFechaEnvioEnd() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getFechaEnvioEnd() + "]");
                //pstmnt.setString(idx++, sc.getFechaEnvioEnd());
            }
            if (sc.getResponsableEjec() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getResponsableEjec() + "]");
                pstmnt.setString(idx++, sc.getResponsableEjec());
            }
            /*Ricardo: Se comenta esta linea para no ligar contra cg_bitacora
			if (sc.getResponsableEjec() != null) { 
				pstmnt.setString(idx++, sc.getResponsableEjec());
			}*/
            if (sc.getResponsableSigte() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getResponsableSigte() + "]");
                pstmnt.setString(idx++, sc.getResponsableSigte());
            }
            if (sc.getReferencia() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getResponsableEjec() + "]");
                pstmnt.setString(idx++, sc.getReferencia());
            }
            if (sc.getAsunto() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getResponsableEjec() + "]");
                //pstmnt.setString(idx++, sc.getAsunto());
            }
            rs = pstmnt.executeQuery();
            if (rs.next())
                TotalRegistros = rs.getInt(1);
            query = "SELECT DISTINCT tc.id_tc" + ",     b.b_id_caso" + ",     b.b_c_folio" + ",     MIN(b.b_c_fecha_ini)   AS b_c_fecha_ini" + ",     MAX(b.b_co_fecha_ini)  AS b_co_fecha_ini" + ",     tc.tc_descripcion AS tc_descripcion" + ",     MIN(CASE WHEN bc.cerrado = 'S' THEN 'cerrado' ELSE 'activo' END) AS estado " + "FROM  cg_tipo_caso tc" + ",     cg_operacion o" + ",     cg_caso_operacion co1" + ",     cg_bitacora b" + ",     cg_bitacora_caso bc" + ",     cg_bitacora_operacion bo " + ("".equals(fromCBO) ? "" : "," + fromCBO) + " " + "WHERE tc.id_tc         = b.b_id_tc " + "AND   o.id_tc          = b.b_id_tc " + "AND   o.id_oper        = b.b_id_oper " + "AND   co1.id_tc        = b.b_id_tc " + "AND   co1.id_caso      = b.b_id_caso " + "AND   co1.id_caso_oper = b.b_co_id_caso_oper_sigte " + "AND   co1.id_caso      = bc.id_caso " + "AND   co1.id_caso      = bo.id_caso " + "AND   co1.id_caso_oper = bo.secuencial_operacion  " + where + ("".equals(whereCBO) ? "" : " " + whereCBO) + " GROUP BY tc.id_tc, b.b_id_caso, b.b_c_folio, tc.tc_descripcion " + "ORDER BY 1,2,3";
            query = //, "
            " SELECT s.tc_id_tc, " + "        s.b_id_caso, " + "        s.b_c_folio, " + "        MIN(s.b_c_fecha_ini)   AS b_c_fecha_ini, " + "	       MAX(s.b_co_fecha_ini)  AS b_co_fecha_ini, " + //+ "        imx.asunto ,"
            "        MIN(CASE WHEN s.bc_cerrado = 'S' THEN 'cerrado' ELSE 'activo' END) AS estado " + //+ "        imx.referencia "
            "  FROM fx_seguimientoConsulta( ?, '') as s" + ("".equals(fromCBO) ? "" : "," + fromCBO) + " ,imxanp imx" + " WHERE " + "  s.c_id_caso = c.id_caso " + "  AND imx.id_gabinete = c.c_id_gabinete" + "  AND  e.ce_os_responsable = co.co_responsable " + "  AND a.id_area           = e.id_area" + where + //,imx.asunto,imx.referencia ";
            ("".equals(whereCBO) ? "" : " " + whereCBO) + " GROUP BY s.tc_id_tc, s.b_id_caso, s.b_c_folio ";
            String query_pag = " SELECT * FROM ( " + "            SELECT *, ROW_NUMBER() OVER (ORDER BY  s.b_c_folio) AS RowNumber " + "              FROM (" + query + " ) as s    ) as seg " + "  WHERE RowNumber BETWEEN " + (in_pd.getTamanoPaginas() * in_pd.getNumeroPagina() + 1) + " AND " + in_pd.getTamanoPaginas() * (in_pd.getNumeroPagina() + 1);
            log.info("Object: {}", "query=[" + query_pag + "]");
            //System.out.println("Query de Seguimiento##### " + query);
            pstmnt = conn.prepareStatement(query_pag);
            idx = 1;
            if (sc.getIdTC() > -1) {
                //log.debug("parameter" + idx + "=[" + sc.getIdTC() + "]");
                pstmnt.setInt(idx++, sc.getIdTC());
            }
            if (sc.getIdOper() > -1) {
                //log.debug("parameter" + idx + "=[" + sc.getIdOper() + "]");
                pstmnt.setInt(idx++, sc.getIdOper());
            }
            if (sc.getIdCaso() > -1) {
                //log.debug("parameter" + idx + "=[" + sc.getIdCaso() + "]");
                pstmnt.setInt(idx++, sc.getIdCaso());
            }
            if ((sc.getFolioIni() != null) && (sc.getFolioEnd() != null)) {
                //log.debug("parameter" + idx + "=[" + sc.getFolioIni() + "]");
                //pstmnt.setString(idx++, sc.getFolioIni());
                //log.debug("parameter" + idx + "=[" + sc.getFolioEnd() + "]");
                //pstmnt.setString(idx++, sc.getFolioEnd());
            } else if (sc.getFolioIni() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getFolioIni() + "]");
                //pstmnt.setString(idx++, sc.getFolioIni());
            } else if (sc.getFolioEnd() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getFolioEnd() + "]");
                //pstmnt.setString(idx++, sc.getFolioEnd());
            }
            if ((sc.getFechaCreacionIni() != null) && (sc.getFechaCreacionEnd() != null)) {
                ////log.debug("parameter" + idx + "=[" + sc.getFechaCreacionIni() + "]");
                //pstmnt.setTimestamp(idx++, sc.getFechaCreacionIni());
                //log.debug("parameter" + idx + "=[" + sc.getFechaCreacionEnd() + "]");
                //pstmnt.setTimestamp(idx++, sc.getFechaCreacionEnd());
            } else if (sc.getFechaCreacionIni() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getFechaCreacionIni() + "]");
                //pstmnt.setString(idx++, sc.getFechaCreacionIni());
            } else if (sc.getFechaCreacionEnd() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getFechaCreacionEnd() + "]");
                //pstmnt.setString(idx++, sc.getFechaCreacionEnd());
            }
            if ((sc.getFechaEnvioIni() != null) && (sc.getFechaEnvioEnd() != null)) {
                ////log.debug("parameter" + idx + "=[" + sc.getFechaEnvioIni() + "]");
                //pstmnt.setTimestamp(idx++, sc.getFechaEnvioIni());
                ////pstmnt.setString(idx++, "Convert(datetime, " + sc.getFechaEnvioIni() + ", 103)");
                ////log.debug("parameter" + idx + "=[" + sc.getFechaEnvioEnd() + "]");
                //pstmnt.setTimestamp(idx++, sc.getFechaEnvioEnd());
                ////pstmnt.setString(idx++, "Convert(datetime, " + sc.getFechaEnvioEnd() + ", 103)");
            } else if (sc.getFechaEnvioIni() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getFechaEnvioIni() + "]");
                //pstmnt.setString(idx++, sc.getFechaEnvioIni());
            } else if (sc.getFechaEnvioEnd() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getFechaEnvioEnd() + "]");
                //pstmnt.setTimestamp(idx++, sc.getFechaEnvioEnd());
                //pstmnt.setString(idx++, "Convert(datetime, " + sc.getFechaEnvioEnd() + ", 103)");
            }
            if (sc.getResponsableEjec() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getResponsableEjec() + "]");
                pstmnt.setString(idx++, sc.getResponsableEjec());
            }
            /*Ricardo: Se comenta esta linea para no ligar contra cg_bitacora
			if (sc.getResponsableEjec() != null) {
				pstmnt.setString(idx++, sc.getResponsableEjec());
			}*/
            if (sc.getResponsableSigte() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getResponsableSigte() + "]");
                pstmnt.setString(idx++, sc.getResponsableSigte());
            }
            if (sc.getReferencia() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getResponsableSigte() + "]");
                pstmnt.setString(idx++, sc.getReferencia());
            }
            if (sc.getAsunto() != null) {
                //log.debug("parameter" + idx + "=[" + sc.getResponsableSigte() + "]");
                //pstmnt.setString(idx++, sc.getAsunto());
            }
            rs = pstmnt.executeQuery();
            //System.out.println("Le manda a la de seguimiento el resultado de la consulta");
            while (rs.next()) {
                SeguimientoConsulta s = new SeguimientoConsulta();
                s.setIdTC(rs.getInt("tc_id_tc"));
                s.setIdCaso(rs.getInt("b_id_caso"));
                s.setFolioIni(rs.getString("b_c_folio"));
                s.setFechaCreacionIni("" + rs.getTimestamp("b_c_fecha_ini"));
                s.setFechaEnvioIni("" + rs.getTimestamp("b_co_fecha_ini"));
                s.setDescripcion(rs.getString("asunto"));
                s.setEstado(rs.getString("estado"));
                //s.setReferencia(rs.getString("referencia"));
                //s.setAsunto(rs.getString("asunto"));
                listSeg.add(s);
            }
            out_pd.setTamanoPaginas(in_pd.getTamanoPaginas());
            out_pd.setNumeroRegistros(TotalRegistros);
            out_pd.setParamConsulta(in_pd.getParamConsulta());
            out_pd.setBuscarFiltro(in_pd.getBuscarFiltro());
            out_pd.setLista(listSeg);
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return out_pd;
    }
}
