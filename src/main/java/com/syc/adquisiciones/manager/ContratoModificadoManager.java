package com.syc.adquisiciones.manager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import com.syc.adquisiciones.core.ContratoModificado;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ContratoModificadoManager {

    private static Logger log = LoggerFactory.getLogger(ContratoModificadoManager.class);

    public boolean existePartidaContratoMod(Connection conn, String cIdContratoDef, int nConsecutivoMod, int nIdlineaCons) throws SQLException {
        boolean resp = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = conn.prepareStatement("select *from mContratoModificadoPartida with(Nolock) where cIdContratoDefinitivo='" + cIdContratoDef + "' and nConsecutivoModificacion=" + nConsecutivoMod + " and nIdLineaConsolidado=" + nIdlineaCons);
            rs = pstm.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstm != null) {
                pstm.close();
            }
            rs = null;
            pstm = null;
        }
        return resp;
    }

    public boolean updatePartidaContMod(Connection conn, String cIdContratoDef, int nConsecutivoMod, List<String> fila) throws SQLException {
        PreparedStatement pstm = null;
        boolean resp = false;
        try {
            pstm = conn.prepareStatement("update mContratoModificadoPartida set cNewUE='" + fila.get(7) + "' where cIdContratoDefinitivo='" + cIdContratoDef + "' and nConsecutivoModificacion=" + nConsecutivoMod + " and nIdLineaConsolidado=" + fila.get(5));
            resp = pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
        }
        return resp;
    }

    public boolean insertPartidaContMod(Connection conn, String cIdContratoDef, int nConsecutivoMod, List<String> fila) throws SQLException {
        PreparedStatement pstm = null;
        boolean resp = false;
        try {
            pstm = conn.prepareStatement("insert into mContratoModificadoPartida (cEjercicio,cIdContrato,cIdContratoDefinitivo,nConsecutivoModificacion,cIdUnidadEjecutora,cIdTipoConsolidado,cIdConsecutivoConsolidado,nIdLineaConsolidado,cDescripcion,cNewUE) values(?,?,?,?,?,?,?,?,?,?) ");
            pstm.setString(1, fila.get(0));
            pstm.setString(2, fila.get(1));
            pstm.setString(3, cIdContratoDef);
            pstm.setInt(4, nConsecutivoMod);
            pstm.setString(5, fila.get(2));
            pstm.setString(6, fila.get(3));
            pstm.setInt(7, Integer.parseInt(fila.get(4)));
            pstm.setInt(8, Integer.parseInt(fila.get(5)));
            pstm.setString(9, fila.get(6));
            pstm.setString(10, fila.get(7));
            resp = pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
        }
        return resp;
    }

    public boolean addPartidasContrato(Connection conn, ContratoModificado contMod, String lineaConsolidado) throws SQLException {
        PreparedStatement pstm = null;
        StringBuilder query = null;
        String[] datosNumCont = (contMod.getcIdContratoDefinitivo()).split("/");
        boolean ispluri = false;
        try {
            query = new StringBuilder();
            query.append("insert into mContratoModificadoPartida (cEjercicio,cIdContrato,cIdContratoDefinitivo,nConsecutivoModificacion" + ",cIdUnidadEjecutora,cIdTipoConsolidado,cIdConsecutivoConsolidado,nIdLineaConsolidado,cDescripcion,nCantidad,mprecioUnitario,cIdSolicitud,cIdLineaSolicitud,mMontoNeto,cNewUE,mTotalAnterior,cIdunidadEjecutoraLinea) " + " select " + " '" + contMod.getcEjercicio() + "' as ejrcicioFiscal ,'" + contMod.getcIdContrato() + "' as cIdcontrato" + ",'" + contMod.getcIdContratoDefinitivo() + "' as cIdContratoDefinitivo," + contMod.getnConsecutivoModificacion() + " as nConsecutivoModificacion " + ",cIdUnidadEjecutora,cIdTipoConsolidado,nIdConsecutivoConsolidado,nidlineaconsolidado " + ",Descripcion+' - '+DescripcionAdicional, ");
            if ("CV".equalsIgnoreCase(contMod.getcIdContrato().substring(0, 2))) {
                query.append(" cantidad,0 pu,null,null,0,null,MontoOriginal,cIdUnidadEjecutoraSolicitud ");
            } else {
                query.append(" 0,PrecioUnitario,null,null,0,null,MontoOriginal,cIdUnidadEjecutoraSolicitud ");
            }
            //Se valida si es un contrato pluri
            if ("PLU".equalsIgnoreCase((contMod.getcIdContratoDefinitivo()).substring(0, 3)) && !(contMod.getcEjercicio()).equalsIgnoreCase(datosNumCont[1])) {
                query.append(" from [fn_mContratoPluModificadoPartidas](?,?) where 1=1 ");
                ispluri = true;
            } else {
                query.append(" from [fn_mContratoModificadoPartidas](?,?,?,?) where 1=1 ");
            }
            query.append(lineaConsolidado);
            pstm = conn.prepareStatement(query.toString());
            if (!ispluri) {
                pstm.setString(1, contMod.getcEjercicio());
                pstm.setString(2, contMod.getcIdContrato());
                pstm.setString(3, contMod.getcIdContratoDefinitivo());
                pstm.setInt(4, contMod.getnConsecutivoModificacion());
            } else {
                pstm.setString(1, contMod.getcIdContratoDefinitivo());
                pstm.setInt(2, contMod.getnConsecutivoModificacion());
            }
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getcEjercicio());
            log.info("Object: {}", "Parametro 2.- " + contMod.getcIdContrato());
            log.info("Object: {}", "Parametro 3.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 4.- " + contMod.getnConsecutivoModificacion());
            return pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
    }

    public boolean addAllPartidasContrato(Connection conn, ContratoModificado contMod) throws SQLException {
        PreparedStatement pstm = null;
        StringBuilder query = null;
        String[] datosNumCont = (contMod.getcIdContratoDefinitivo()).split("/");
        boolean ispluri = false;
        try {
            query = new StringBuilder();
            query.append("insert into mContratoModificadoPartida (cEjercicio,cIdContrato,cIdContratoDefinitivo,nConsecutivoModificacion" + ",cIdUnidadEjecutora,cIdTipoConsolidado,cIdConsecutivoConsolidado,nIdLineaConsolidado,cDescripcion,nCantidad,mprecioUnitario,cIdSolicitud,cIdLineaSolicitud,mMontoNeto,cNewUE,mTotalAnterior,cIdunidadEjecutoraLinea) " + " select " + " '" + contMod.getcEjercicio() + "' as ejrcicioFiscal ,'" + contMod.getcIdContrato() + "' as cIdcontrato" + ",'" + contMod.getcIdContratoDefinitivo() + "' as cIdContratoDefinitivo," + contMod.getnConsecutivoModificacion() + " as nConsecutivoModificacion " + ",cIdUnidadEjecutora,cIdTipoConsolidado,nIdConsecutivoConsolidado,nidlineaconsolidado " + ",Descripcion+' - '+DescripcionAdicional, ");
            if ("CV".equalsIgnoreCase(contMod.getcIdContrato().substring(0, 2))) {
                query.append(" cantidad,0 pu,null,null,0,null,MontoOriginal,cIdUnidadEjecutoraSolicitud ");
            } else {
                query.append(" 0,PrecioUnitario,null,null,0,null,MontoOriginal,cIdUnidadEjecutoraSolicitud ");
            }
            //Se valida si es un contrato pluri
            if ("PLU".equalsIgnoreCase((contMod.getcIdContratoDefinitivo()).substring(0, 3)) && !(contMod.getcEjercicio()).equalsIgnoreCase(datosNumCont[1])) {
                query.append(" from [fn_mContratoPluModificadoPartidas](?,?) where 1=1 ");
                ispluri = true;
            } else {
                query.append(" from [fn_mContratoModificadoPartidas](?,?,?,?) where 1=1 ");
            }
            pstm = conn.prepareStatement(query.toString());
            if (!ispluri) {
                pstm.setString(1, contMod.getcEjercicio());
                pstm.setString(2, contMod.getcIdContrato());
                pstm.setString(3, contMod.getcIdContratoDefinitivo());
                pstm.setInt(4, contMod.getnConsecutivoModificacion());
            } else {
                pstm.setString(1, contMod.getcIdContratoDefinitivo());
                pstm.setInt(2, contMod.getnConsecutivoModificacion());
            }
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getcEjercicio());
            log.info("Object: {}", "Parametro 2.- " + contMod.getcIdContrato());
            log.info("Object: {}", "Parametro 3.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 4.- " + contMod.getnConsecutivoModificacion());
            return pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
    }

    public boolean deletePartidaContMod(Connection conn, ContratoModificado contMod, int lineaConsolidado) throws SQLException {
        PreparedStatement pstm = null;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("DELETE FROM mContratoModificadoPartida " + "WHERE cIdContratoDefinitivo = ? " + "and nConsecutivoModificacion = ? " + "and nIdLineaConsolidado = ?");
            pstm = conn.prepareStatement(query.toString());
            pstm.setString(1, contMod.getcIdContratoDefinitivo());
            pstm.setInt(2, contMod.getnConsecutivoModificacion());
            pstm.setInt(3, lineaConsolidado);
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 2.- " + contMod.getnConsecutivoModificacion());
            log.info("Object: {}", "Parametro 3.- " + lineaConsolidado);
            return pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
    }

    public boolean deleteAllPartidasContMod(Connection conn, ContratoModificado contMod) throws SQLException {
        PreparedStatement pstm = null;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("DELETE FROM mContratoModificadoPartida " + "WHERE cIdContratoDefinitivo = ? " + "and nConsecutivoModificacion = ? ");
            pstm = conn.prepareStatement(query.toString());
            pstm.setString(1, contMod.getcIdContratoDefinitivo());
            pstm.setInt(2, contMod.getnConsecutivoModificacion());
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 2.- " + contMod.getnConsecutivoModificacion());
            return pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
    }

    public boolean updatePartidaContMod(Connection conn, ContratoModificado contMod, List<String> fila) throws SQLException {
        PreparedStatement pstm = null;
        StringBuilder query = null;
        /* Posiciones de la fila
		 * 0.- Linea de consolidado
		 * 1.- Monto con IVA original de la partida del contrato
		 * 2.- IVA
		 * 3.- Cantidad a Modificar
		 * 4.- Precio Unitario
		 * 5.- Monto con IVA capturado, monto a reducir
		 * 6.- Cantidad Original
		 * */
        try {
            query = new StringBuilder();
            query.append("update mContratoModificadoPartida ");
            if ("CV".equalsIgnoreCase(contMod.getcIdContrato().substring(0, 2))) {
                //Servicios
                query.append("set mTotalAnterior=round(" + fila.get(1) + ",2),mMontoNeto=-1*" + fila.get(5) + ",mPrecioUnitario=-1*round((" + fila.get(5) + "/nCantidad/(1+(0.01*" + fila.get(2) + "))),2)");
            } else {
                query.append("set nCantidad=-1*" + fila.get(3) + ",mMontoNeto=-1*round(mPrecioUnitario*" + fila.get(3) + "*(1+(0.01*" + fila.get(2) + ")),2)");
            }
            query.append(" where cIdContratoDefinitivo=? and nConsecutivoModificacion=? and nIdLineaConsolidado=?");
            pstm = conn.prepareStatement(query.toString());
            pstm.setString(1, contMod.getcIdContratoDefinitivo());
            pstm.setInt(2, contMod.getnConsecutivoModificacion());
            pstm.setInt(3, Integer.parseInt((fila.get(0))));
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 2.- " + contMod.getnConsecutivoModificacion());
            log.info("Object: {}", "Parametro 3.- " + fila.get(0));
            return pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
    }

    public boolean updateContMod(Connection conn, ContratoModificado contMod) throws SQLException {
        PreparedStatement pstm = null;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mContratoModificado set mTotalNuevo=mTotalAnterior - (" + contMod.getmTotalModificacion() + "),mTotalModificacion=-(" + contMod.getmTotalModificacion());
            query.append(") where cIdContratoDefinitivo=? and nConsecutivoModificacion=? ");
            pstm = conn.prepareStatement(query.toString());
            pstm.setString(1, contMod.getcIdContratoDefinitivo());
            pstm.setInt(2, contMod.getnConsecutivoModificacion());
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 2.- " + contMod.getnConsecutivoModificacion());
            return pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
    }

    public boolean updateContModDeletePart(Connection conn, ContratoModificado contMod) throws SQLException {
        PreparedStatement pstm = null;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mContratoModificado set mTotalNuevo=mTotalNuevo + (" + contMod.getmTotalModificacion() + "),mTotalModificacion=mTotalModificacion-(" + contMod.getmTotalModificacion());
            query.append(") where cIdContratoDefinitivo=? and nConsecutivoModificacion=? ");
            pstm = conn.prepareStatement(query.toString());
            pstm.setString(1, contMod.getcIdContratoDefinitivo());
            pstm.setInt(2, contMod.getnConsecutivoModificacion());
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 2.- " + contMod.getnConsecutivoModificacion());
            return pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
    }

    public double queryMontoModificadoLinea(Connection conn, ContratoModificado contMod, int nidlineaConsolidado) throws SQLException {
        double resp = 0.0;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("select isnull(mMontoNeto,0) totalMod from mContratoModificadoPartida with(Nolock) where cIdContratoDefinitivo=? and nConsecutivoModificacion=? and nidlineaConsolidado=?");
            pstm = conn.prepareStatement(query.toString());
            pstm.setString(1, contMod.getcIdContratoDefinitivo());
            pstm.setInt(2, contMod.getnConsecutivoModificacion());
            pstm.setInt(3, nidlineaConsolidado);
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 2.- " + contMod.getnConsecutivoModificacion());
            log.info("Object: {}", "Parametro 3.- " + nidlineaConsolidado);
            rs = pstm.executeQuery();
            if (rs.next()) {
                resp = rs.getDouble("totalMod");
                log.info("Object: {}", "Monto Modificado Total=" + resp);
            }
            return resp;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstm != null) {
                pstm.close();
            }
            rs = null;
            pstm = null;
            query = null;
        }
    }

    public boolean updateStateConvMod(Connection conn, ContratoModificado contMod) throws SQLException {
        PreparedStatement pstm = null;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mContratoModificado set nEstado=? where cIdContratoDefinitivo=? and nConsecutivoModificacion=? ");
            pstm = conn.prepareStatement(query.toString());
            pstm.setInt(1, contMod.getnEstado());
            pstm.setString(2, contMod.getcIdContratoDefinitivo());
            pstm.setInt(3, contMod.getnConsecutivoModificacion());
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getnEstado());
            log.info("Object: {}", "Parametro 2.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 3.- " + contMod.getnConsecutivoModificacion());
            return pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
    }

    public boolean addCalendarioCaptura(Connection conn, ContratoModificado contMod, JSONArray jsonArrayMeses, String cIdClaveEgresos, String cClaveInterna) throws SQLException, JSONException {
        PreparedStatement pstm = null;
        StringBuilder queryParam = null;
        StringBuilder queryValue = null;
        try {
            queryParam = new StringBuilder();
            queryValue = new StringBuilder();
            queryParam.append("insert into mContratoModificadoPresupuestoCapturado (cIdcontratoDefinitivo,nIdconsecutivoMod,cIdClaveEgresos,cClaveInterna");
            queryValue.append(" values(?,?,?,?");
            for (int j = 0; j < jsonArrayMeses.length(); j++) {
                queryParam.append("," + jsonArrayMeses.getJSONObject(j).getString("nameMes").substring(0, 5));
                queryValue.append(",-" + jsonArrayMeses.getJSONObject(j).getDouble("value"));
            }
            queryParam.append(" ) ");
            queryValue.append(" ) ");
            pstm = conn.prepareStatement(queryParam.toString() + " " + queryValue.toString());
            pstm.setString(1, contMod.getcIdContratoDefinitivo());
            pstm.setInt(2, contMod.getnConsecutivoModificacion());
            pstm.setString(3, cIdClaveEgresos);
            pstm.setString(4, cClaveInterna);
            log.info("Object: {}", queryParam.toString() + " " + queryValue.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 2.- " + contMod.getnConsecutivoModificacion());
            log.info("Object: {}", "Parametro 3.- " + cIdClaveEgresos);
            log.info("Object: {}", "Parametro 4.- " + cClaveInterna);
            return pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            queryParam = null;
            queryValue = null;
        }
    }

    public boolean deleteCalendarioCaptura(Connection conn, ContratoModificado contMod) throws SQLException {
        PreparedStatement pstm = null;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("delete mContratoModificadoPresupuestoCapturado where cIdcontratoDefinitivo=? and nIdconsecutivoMod=? ");
            pstm = conn.prepareStatement(query.toString());
            pstm.setString(1, contMod.getcIdContratoDefinitivo());
            pstm.setInt(2, contMod.getnConsecutivoModificacion());
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 2.- " + contMod.getnConsecutivoModificacion());
            return pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
    }

    public ArrayList<Map<String, String>> queryUnidadesCont(Connection conn, ContratoModificado contMod) throws SQLException {
        PreparedStatement pstm = null;
        StringBuilder query = null;
        ResultSet rs = null;
        Map<String, String> datos = null;
        ArrayList<Map<String, String>> registros = null;
        try {
            query = new StringBuilder();
            if (contMod.getnEsDescentralizado() == 1) {
                query.append("select cc.cUnidadResponsable cUnidadResponsable,cc.cCentroContable from mContratoModificadoPartida part with(Nolock) ");
                query.append("inner join tCatalogoURCC as cc with(Nolock) on (cc.cUnidadResponsable=part.cIdunidadEjecutoraLinea or cc.cUnidadResponsable=SUBSTRING(part.cIdSolicitud,4,3)) ");
                query.append("where cIdContratoDefinitivo=? and nConsecutivoModificacion=? ");
                query.append("group by cc.cUnidadResponsable,cc.cCentroContable ");
            } else {
                query.append(" select part.cIdUnidadEjecutora cUnidadResponsable,cc.cCentroContable  ");
                query.append(" from mContratoModificadoPartida part with(Nolock) ");
                query.append(" inner join tCatalogoURCC as cc with(Nolock) on (cc.cUnidadResponsable=part.cIdUnidadEjecutora) ");
                query.append(" where cIdContratoDefinitivo=? and nConsecutivoModificacion=? ");
                query.append(" group by part.cIdUnidadEjecutora ,cc.cCentroContable ");
            }
            pstm = conn.prepareStatement(query.toString());
            pstm.setString(1, contMod.getcIdContratoDefinitivo());
            pstm.setInt(2, contMod.getnConsecutivoModificacion());
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 2.- " + contMod.getnConsecutivoModificacion());
            rs = pstm.executeQuery();
            datos = new HashMap<String, String>();
            registros = new ArrayList<Map<String, String>>();
            while (rs.next()) {
                datos = new HashMap<String, String>();
                datos.put("cUnidadResponsable", rs.getString("cUnidadResponsable"));
                datos.put("cCentroContable", rs.getString("cCentroContable"));
                registros.add(datos);
                datos = null;
            }
            return registros;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (rs != null) {
                rs.close();
            }
            pstm = null;
            query = null;
            rs = null;
            datos = null;
        }
    }

    public synchronized Caso iniciaCaso(Usuario user, String ur, String tCaso, String jndiName) throws GestionException {
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jndiName);
        if (tCaso == null) {
            log.error("Identificador de Tipo de Caso, vacio");
            throw new GestionException("Identificador de Tipo de Caso, vacio");
        }
        int idTC = Integer.parseInt(tCaso);
        if (idTC <= 0) {
            log.error("Identificador de Tipo de Caso, menor o igual a cero (<= 0)");
            throw new GestionException("Identificador de Tipo de Caso, menor o igual a cero (<= 0)");
        }
        Caso c = casoTx.IniciaCaso(user.getNombre(), ur, idTC);
        if (c == null) {
            log.error("No se logro crear el caso");
            throw new GestionException("No se logró crear el caso");
        }
        return c;
    }

    public boolean createEncabezadoCompromisoRed(Connection conn, ContratoModificado contMod, String cLogin) throws SQLException {
        PreparedStatement pstm = null;
        StringBuilder queryEnc = null;
        try {
            queryEnc = new StringBuilder();
            //Crear el encabezado del compromiso
            queryEnc.append("insert into tCompromisoEncabezado (nFolioCompromiso,fCarga,cIdContrato,cTipoContrato,fAplicacion ");
            queryEnc.append(",cCentroContable,cRamo,cUnidadResponsable,cDocumentoHaplicado,nFolioPoliza,caNoCompromiso,nEnviadoSICOP,cTipoPoliza ");
            queryEnc.append(",nMes,cRevisado,aEjercicioFiscal,cUnidadResponsableContable,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,usuario,cRadicado,nFolioAutSICOP) ");
            queryEnc.append("select tOP(1) " + contMod.getnFolioCompromiso() + " nFolioCompromiso,convert(date,GETDATE())fCarga,cIdContrato,cTipoContrato ");
            queryEnc.append(",convert(date,GETDATE())fAplicacion,cCentroContable,cRamo,cUnidadResponsable,null aplicado,null foliopoliza ");
            queryEnc.append(",'" + contMod.getcCanoCompromiso() + "'caNoCompromiso,0 enviadoSIcop,cTipoPoliza,MONTH(GETDATE())nMes,null crevisado ");
            queryEnc.append(",aEjercicioFiscal,cUnidadResponsableContable,null nFolioPolizaCancelacion ");
            queryEnc.append(",null fCancelacion,'REGISTRO DE CONVENIO DE REDUCCIÓN' DESCRIP ");
            queryEnc.append(",'" + cLogin + "'usuario,cRadicado,null nfolioAutSICOP ");
            queryEnc.append("from tCompromisoEncabezado with(Nolock) where cIdContrato=? and cUnidadResponsable=?");
            pstm = conn.prepareStatement(queryEnc.toString());
            pstm.setString(1, contMod.getcIdContratoDefinitivo());
            pstm.setString(2, contMod.getcUnidadEjecutoraLinea());
            log.info("Object: {}", queryEnc.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 2.- " + contMod.getcUnidadEjecutoraLinea());
            return pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            queryEnc = null;
        }
    }

    public boolean createDetalleCompromisoRed(Connection conn, ContratoModificado contMod, String cEvento) throws SQLException {
        PreparedStatement pstm = null;
        StringBuilder queryDet = null;
        try {
            queryDet = new StringBuilder();
            //Crear el encabezado del compromiso
            queryDet.append("insert into tCompromisoDetalle (nFolioCompromiso,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable,cUnidadResponsable) ");
            queryDet.append("select " + contMod.getnFolioCompromiso() + " nFolio,ROW_NUMBER() OVER (ORDER BY EP,mes)nDocRenglon ");
            queryDet.append(",EP,evento,mimporte,mimporteNegativo,mes,'" + contMod.getcCentroContable() + "' centroContable,'" + contMod.getcUnidadEjecutoraLinea() + "'unidad ");
            queryDet.append("from(select cIdClaveEgresos+'.'+cClaveInterna EP,'" + cEvento + "' evento ");
            queryDet.append(",Orders mimporte,Orders*-1 mimporteNegativo ");
            queryDet.append(",convert(int,REPLACE(Mes,'mes',''))mes ");
            queryDet.append("from (select *from mContratoModificadoPresupuestoCapturado cap with(Nolock)  ");
            queryDet.append("where cIdContratoDefinitivo=? and SUBSTRING(cClaveInterna,1,3)=? ");
            queryDet.append(")sub ");
            queryDet.append("UNPIVOT ");
            queryDet.append("(Orders FOR Mes IN ");
            queryDet.append("(mes01, mes02, mes03, mes04, mes05,mes06,mes07,mes08,mes09,mes10,mes11,mes12) ");
            queryDet.append(")AS unpvt ");
            queryDet.append(")sub ");
            queryDet.append("where sub.mimporte<>0");
            pstm = conn.prepareStatement(queryDet.toString());
            pstm.setString(1, contMod.getcIdContratoDefinitivo());
            pstm.setString(2, contMod.getcUnidadEjecutoraLinea());
            log.info("Object: {}", queryDet.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 2.- " + contMod.getcUnidadEjecutoraLinea());
            return pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            queryDet = null;
        }
    }

    public boolean createEncabezadoPrecomFinanciero(Connection conn, ContratoModificado contMod, String cLogin) throws SQLException {
        PreparedStatement pstm = null;
        StringBuilder queryEnc = null;
        try {
            queryEnc = new StringBuilder();
            //Crear el encabezado del compromiso
            queryEnc.append("insert into tPrecomFinancieroEncabezado (nFolioPrecomFinanciero,fCarga,fAplicacion,cCentroContable,cRamo,cUnidadResponsable,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,aEjercicioFiscal ");
            queryEnc.append(",cUnidadResponsableContable,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,U_LOGIN,cRadicado) ");
            queryEnc.append("select tOP(1) " + contMod.getnFolioCompromiso() + " nFolioCompromiso,convert(date,GETDATE())fCarga,convert(date,GETDATE())fAplicacion ");
            queryEnc.append(",cCentroContable,cRamo,cUnidadResponsable,null aplicado,null foliopoliza ");
            queryEnc.append(",'PR'cTipoPoliza,aEjercicioFiscal,cUnidadResponsableContable,null nFolioPolizaCancelacion ");
            queryEnc.append(",null fCancelacion,'REGISTRO DE CONVENIO DE REDUCCIÓN'DESCRIP ");
            queryEnc.append(",'" + cLogin + "'usuario,null cRadicado ");
            queryEnc.append("from tCompromisoEncabezado with(Nolock) where cIdContrato=? and cUnidadResponsable=? ");
            pstm = conn.prepareStatement(queryEnc.toString());
            pstm.setString(1, contMod.getcIdContratoDefinitivo());
            pstm.setString(2, contMod.getcUnidadEjecutoraLinea());
            log.info("Object: {}", queryEnc.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 2.- " + contMod.getcUnidadEjecutoraLinea());
            return pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            queryEnc = null;
        }
    }

    public boolean createDetallePrecomFinanciero(Connection conn, ContratoModificado contMod, String cEvento) throws SQLException {
        PreparedStatement pstm = null;
        StringBuilder queryDet = null;
        try {
            queryDet = new StringBuilder();
            //Crear el encabezado del compromiso
            queryDet.append("insert into tPrecomFinancieroDetalle (nFolioPrecomFinanciero,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable,cUnidadResponsable) ");
            queryDet.append("select " + contMod.getnFolioCompromiso() + " nFolio,ROW_NUMBER() OVER (ORDER BY EP,mes)nDocRenglon ");
            queryDet.append(",EP,evento,mimporte,mimporteNegativo,mes,'" + contMod.getcCentroContable() + "' centroContable,'" + contMod.getcUnidadEjecutoraLinea() + "'unidad ");
            queryDet.append("from(select cIdClaveEgresos+'.'+cClaveInterna EP,'" + cEvento + "' evento");
            queryDet.append(",Orders mimporte,Orders*-1 mimporteNegativo ");
            queryDet.append(",convert(int,REPLACE(Mes,'mes',''))mes ");
            queryDet.append("from (select *from mContratoModificadoPresupuestoCapturado cap with(Nolock)  ");
            queryDet.append("where cIdContratoDefinitivo=? and SUBSTRING(cClaveInterna,1,3)=? ");
            queryDet.append(")sub ");
            queryDet.append("UNPIVOT ");
            queryDet.append("(Orders FOR Mes IN ");
            queryDet.append("(mes01, mes02, mes03, mes04, mes05,mes06,mes07,mes08,mes09,mes10,mes11,mes12) ");
            queryDet.append(")AS unpvt ");
            queryDet.append(")sub ");
            queryDet.append("where sub.mimporte<>0");
            pstm = conn.prepareStatement(queryDet.toString());
            pstm.setString(1, contMod.getcIdContratoDefinitivo());
            pstm.setString(2, contMod.getcUnidadEjecutoraLinea());
            log.info("Object: {}", queryDet.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 2.- " + contMod.getcUnidadEjecutoraLinea());
            return pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            queryDet = null;
        }
    }

    public void validateDates(Connection conn, ContratoModificado contMod) throws Exception {
        CallableStatement cmst = null;
        StringBuilder query = null;
        int outputValue = -1;
        try {
            query = new StringBuilder();
            //Crear el encabezado del compromiso
            query.append("{?= call pa_revisaFechasModificatorio (?,?,?,?)}");
            cmst = conn.prepareCall(query.toString());
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, contMod.getfFechaFormalizacion());
            cmst.setString(3, contMod.getfFechaInicio());
            cmst.setString(4, contMod.getfFechaFin());
            cmst.setString(5, contMod.getcIdContratoDefinitivo());
            cmst.execute();
            outputValue = cmst.getInt(1);
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "Parametro 2.- " + contMod.getfFechaFormalizacion());
            log.info("Object: {}", "Parametro 3.- " + contMod.getfFechaInicio());
            log.info("Object: {}", "Parametro 4.- " + contMod.getfFechaFin());
            log.info("Object: {}", "Parametro 5.- " + contMod.getcIdContratoDefinitivo());
            if (outputValue != 0) {
                if (outputValue == 1) {
                    throw new Exception("La fecha fin no puede ser menor a la fecha inicio.");
                }
                if (outputValue == 2) {
                    throw new Exception("La fecha de formalizaci\u00f3n debe de estar dentro de la vigencia del contrato.");
                }
            }
        } finally {
            if (cmst != null) {
                cmst.close();
            }
            cmst = null;
            query = null;
        }
    }

    public boolean saveDates(Connection conn, ContratoModificado contMod) throws SQLException {
        PreparedStatement pstm = null;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update pContratoDiversoConvenio set fInicio=CONVERT(date,?),fTermino=CONVERT(date,?),fFirmaContrato=CONVERT(date,?),fAdjudicacion=CONVERT(date,?) ");
            query.append("where cIdContrato=? and nConsecutivoModificacion=? and cIdModificacion=?");
            pstm = conn.prepareStatement(query.toString());
            pstm.setString(1, contMod.getfFechaInicio());
            pstm.setString(2, contMod.getfFechaFin());
            pstm.setString(3, contMod.getfFechaFormalizacion());
            pstm.setString(4, contMod.getfFechaFormalizacion());
            pstm.setString(5, contMod.getcIdContratoDefinitivo());
            pstm.setInt(6, contMod.getnConsecutivoModificacion());
            pstm.setString(7, contMod.getcIdContratoDefinitivo() + "#M" + contMod.getnConsecutivoModificacion());
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getfFechaInicio());
            log.info("Object: {}", "Parametro 2.- " + contMod.getfFechaFin());
            log.info("Object: {}", "Parametro 3.- " + contMod.getfFechaFormalizacion());
            log.info("Object: {}", "Parametro 4.- " + contMod.getfFechaFormalizacion());
            log.info("Object: {}", "Parametro 5.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 6.- " + contMod.getnConsecutivoModificacion());
            log.info("Object: {}", "Parametro 7.- " + contMod.getcIdContratoDefinitivo() + "#M" + contMod.getnConsecutivoModificacion());
            return pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
    }

    public boolean saveNumConvenio(Connection conn, ContratoModificado contMod) throws SQLException {
        PreparedStatement pstm = null;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" update mContratoModificado set cNoConvenio=?,cObjetoConvenio=? where cIdContratoDefinitivo=? and nConsecutivoModificacion=?");
            pstm = conn.prepareStatement(query.toString());
            pstm.setString(1, contMod.getcNoConvenio());
            pstm.setString(2, contMod.getcObjetoConv());
            pstm.setString(3, contMod.getcIdContratoDefinitivo());
            pstm.setInt(4, contMod.getnConsecutivoModificacion());
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "Parametro 1.- " + contMod.getcNoConvenio());
            log.info("Object: {}", "Parametro 2.- " + contMod.getcObjetoConv());
            log.info("Object: {}", "Parametro 3.- " + contMod.getcIdContratoDefinitivo());
            log.info("Object: {}", "Parametro 4.- " + contMod.getnConsecutivoModificacion());
            return pstm.executeUpdate() > 0;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
    }
}
