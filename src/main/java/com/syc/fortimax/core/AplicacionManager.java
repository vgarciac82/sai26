package com.syc.fortimax.core;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import com.syc.adquisiciones.DatosCaso;
import com.syc.dbms.DBMS;
import com.syc.gestion.CasoOperacionBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDato;
import com.syc.gestion.core.Cobertura;
import com.syc.gestion.core.CoberturaManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.PaginaData;
import com.syc.gestion.util.Util;
import java.util.Base64;

public class AplicacionManager {

    public static void aprobarRechazarVigencia(Connection conn, String folio, boolean aprobar, String motivo) throws SQLException {
        PreparedStatement ps = null;
        try {
            if (aprobar) {
                ps = conn.prepareStatement("update mPrecompromisoVigencia set aprobada = ? where aprobada = ? and nFolioPreCompromiso = ?");
                ps.setString(1, "1");
                ps.setString(2, "0");
                ps.setString(3, folio);
            } else {
                ps = conn.prepareStatement("update mPrecompromisoVigencia set aprobada = ?, motivo = ? where aprobada = ? and nFolioPreCompromiso = ?");
                ps.setString(1, "2");
                ps.setString(2, motivo);
                ps.setString(3, "0");
                ps.setString(4, folio);
            }
            ps.execute();
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        } finally {
            if (ps != null)
                ps.close();
            ps = null;
        }
    }

    public static void creaEstructuraCarpeta(Connection conn, String titulo_aplicacion, int id_gabinete, String nombre_usuario, String nombre_carpeta_raiz) throws SQLException {
        creaEstructuraCarpeta(conn, titulo_aplicacion, id_gabinete, nombre_usuario, nombre_carpeta_raiz, null);
    }

    public static void creaEstructuraCarpeta(Connection conn, String titulo_aplicacion, int id_gabinete, String nombre_usuario, String nombre_carpeta_raiz, String nombre_estructura) throws SQLException {
        PreparedStatement pstmntMain = null;
        PreparedStatement pstmntDoc = null;
        PreparedStatement pstmntAux = null;
        ResultSet rsMain = null;
        ResultSet rsDoc = null;
        int countIdCarpeta = 0;
        String sAnd = "";
        if (nombre_estructura != null) {
            sAnd = " AND NOMBRE_ESTRUCTURA = ? ";
        }
        try {
            pstmntMain = conn.prepareStatement("SELECT COUNT(nombre_elemento) FROM imx_estruc_doctos " + "WHERE titulo_aplicacion = ? " + sAnd);
            pstmntDoc = conn.prepareStatement("SELECT id_tipo_docto FROM imx_tipo_documento " + "WHERE titulo_aplicacion = ? AND nombre_tipo_docto = ?");
            pstmntMain.setString(1, titulo_aplicacion);
            if (nombre_estructura != null) {
                pstmntMain.setString(2, nombre_estructura);
            }
            rsMain = pstmntMain.executeQuery();
            if (!rsMain.next())
                throw new SQLException("No existe la tabla 'imx_estruc_doctos'");
            if (rsMain.getInt(1) > 0) {
                int[] arrIdCarpetaPadre = new int[rsMain.getInt(1)];
                int[] arrIdDocumento = new int[rsMain.getInt(1)];
                pstmntMain = conn.prepareStatement("SELECT prioridad, profundidad - 1, nombre_elemento, descripcion " + "FROM imx_estruc_doctos WHERE titulo_aplicacion = ? " + sAnd + "ORDER BY nombre_estructura, posicion_elemento");
                pstmntMain.setString(1, titulo_aplicacion);
                if (nombre_estructura != null) {
                    pstmntMain.setString(2, nombre_estructura);
                }
                rsMain = pstmntMain.executeQuery();
                while (rsMain.next()) {
                    int prioridad = rsMain.getInt(1);
                    int profundidad = rsMain.getInt(2);
                    String nombre_elemento = rsMain.getString(3);
                    String descripcion = rsMain.getString(4);
                    if (prioridad == -1) {
                        // Carpeta
                        arrIdCarpetaPadre[profundidad] = ++countIdCarpeta;
                        pstmntAux = conn.prepareStatement("INSERT INTO imx_carpeta " + "( titulo_aplicacion, id_gabinete, id_carpeta" + ", nombre_carpeta, nombre_usuario, bandera_raiz" + ", fh_creacion, fh_modificacion, numero_accesos" + ", numero_carpetas, numero_documentos, descripcion) " + "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        pstmntAux.setString(1, titulo_aplicacion);
                        pstmntAux.setInt(2, id_gabinete);
                        pstmntAux.setInt(3, countIdCarpeta);
                        pstmntAux.setString(4, (countIdCarpeta == 0) ? nombre_carpeta_raiz : nombre_elemento);
                        pstmntAux.setString(5, nombre_usuario);
                        pstmntAux.setString(6, (countIdCarpeta == 0) ? "S" : "N");
                        pstmntAux.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
                        pstmntAux.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                        pstmntAux.setInt(9, 0);
                        pstmntAux.setInt(10, 0);
                        pstmntAux.setInt(11, 0);
                        pstmntAux.setString(12, descripcion);
                        pstmntAux.executeUpdate();
                        pstmntAux = conn.prepareStatement("INSERT INTO imx_org_carpeta " + "( titulo_aplicacion, id_gabinete, id_carpeta_hija," + " id_carpeta_padre, nombre_hija) " + "VALUES (?, ?, ?, ?, ?)");
                        pstmntAux.setString(1, titulo_aplicacion);
                        pstmntAux.setInt(2, id_gabinete);
                        pstmntAux.setInt(3, countIdCarpeta);
                        pstmntAux.setInt(4, (countIdCarpeta == 1 ? 0 : arrIdCarpetaPadre[profundidad - 1]));
                        pstmntAux.setString(5, nombre_elemento);
                        pstmntAux.executeUpdate();
                    } else if (prioridad >= 0) {
                        // Documento
                        //int id_tipo_docto = -1; Ethiel, por default le pone tipo imax cuando no existe
                        int id_tipo_docto = 1;
                        pstmntDoc.setString(1, titulo_aplicacion);
                        pstmntDoc.setString(2, nombre_elemento);
                        rsDoc = pstmntDoc.executeQuery();
                        if (rsDoc.next())
                            id_tipo_docto = rsDoc.getInt(1);
                        arrIdDocumento[arrIdCarpetaPadre[profundidad - 1]]++;
                        pstmntAux = conn.prepareStatement("INSERT INTO imx_documento " + "( titulo_aplicacion" + ", id_gabinete" + ", id_carpeta_padre" + ", id_documento" + ", nombre_documento" + ", nombre_usuario" + ", prioridad" + ", id_tipo_docto" + ", fh_creacion" + ", fh_modificacion" + ", numero_accesos" + ", numero_paginas" + ", titulo" + ", autor" + ", materia" + ", descripcion" + ", clase_documento" + ", estado_documento" + ", tamano_bytes)" + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        pstmntAux.setString(1, titulo_aplicacion);
                        pstmntAux.setInt(2, id_gabinete);
                        pstmntAux.setInt(3, arrIdCarpetaPadre[profundidad - 1]);
                        pstmntAux.setInt(4, arrIdDocumento[arrIdCarpetaPadre[profundidad - 1]]);
                        pstmntAux.setString(5, nombre_elemento);
                        pstmntAux.setString(6, nombre_usuario);
                        pstmntAux.setInt(7, prioridad);
                        pstmntAux.setInt(8, id_tipo_docto);
                        pstmntAux.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
                        pstmntAux.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
                        pstmntAux.setInt(11, 0);
                        pstmntAux.setInt(12, 0);
                        pstmntAux.setNull(13, Types.VARCHAR);
                        pstmntAux.setNull(14, Types.VARCHAR);
                        pstmntAux.setString(15, "ORIGINAL");
                        pstmntAux.setString(16, descripcion);
                        pstmntAux.setInt(17, 0);
                        pstmntAux.setString(18, "V");
                        pstmntAux.setDouble(19, 0);
                        pstmntAux.executeUpdate();
                    }
                }
            }
        } finally {
            if (pstmntAux != null)
                pstmntAux.close();
            if (rsDoc != null)
                rsDoc.close();
            if (rsMain != null)
                rsMain.close();
            if (pstmntDoc != null)
                pstmntDoc.close();
            if (pstmntMain != null)
                pstmntMain.close();
            pstmntAux = null;
            rsDoc = null;
            rsMain = null;
            pstmntDoc = null;
            pstmntMain = null;
        }
    }

    public static int createExpediente(Connection conn, String u_login, Caso c, Aplicacion app) throws SQLException {
        int id_gabinete = -1;
        PreparedStatement pstmnt = null;
        PreparedStatement pstmntFolder = null;
        try {
            Statement stmnt = null;
            ResultSet rs = null;
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery("SELECT MAX(id_gabinete) FROM " + app.getTableAplicacion());
            if (!rs.next())
                throw new SQLException("No se logro recuperar el identificador de expediente (" + app.getTableAplicacion() + ")");
            id_gabinete = rs.getInt(1) + 1;
            Set<?> data = c.getCasoDato().keySet();
            StringBuffer valueList = new StringBuffer(id_gabinete + ", 'N'");
            StringBuffer fieldList = new StringBuffer("id_gabinete, activo");
            for (Iterator<?> iter = data.iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                CasoDato cd = (CasoDato) c.getCasoDato(name);
                if (!cd.getTipoCasoVariable().isEnGaveta())
                    continue;
                fieldList.append(", " + name);
                valueList.append(", ?");
            }
            pstmnt = conn.prepareStatement("INSERT INTO " + app.getTableAplicacion() + " (" + fieldList + ") VALUES (" + valueList + ")");
            int i = 0;
            for (Iterator iter = data.iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                CasoDato cd = (CasoDato) c.getCasoDato(name);
                if (!cd.getTipoCasoVariable().isEnGaveta())
                    continue;
                i++;
                int tcv_tipo = cd.getTipoCasoVariable().getTipo();
                switch(tcv_tipo) {
                    // Small Integer
                    case 3:
                    case // Long Integer
                    4:
                        pstmnt.setInt(i, Integer.parseInt(cd.getValor()));
                        break;
                    // Decimal
                    case 5:
                    case // Double, Float
                    7:
                        pstmnt.setLong(i, Long.parseLong(cd.getValor()));
                        break;
                    case // Date
                    8:
                        if (cd.getValor() == null) {
                            throw new SQLException("Fecha nula en variable \"" + name + "\"");
                        } else if (("sysdate".equalsIgnoreCase(cd.getValor()))) {
                            pstmnt.setTimestamp(i, new Timestamp(System.currentTimeMillis()));
                        } else if (("".equalsIgnoreCase(cd.getValor()))) {
                            //Ethiel, para que no ponga una fecha de 1969
                            pstmnt.setTimestamp(i, null);
                        } else {
                            long miFecha = -1;
                            try {
                                miFecha = Long.parseLong(cd.getValor());
                            } catch (NumberFormatException nfe) {
                                //quiere decir que es una fecha?
                                if (cd.getValor().length() == 10) {
                                    //OJO: ESTO DEBE DE VERIFICAR EL FORMATO DEFINIDO
                                    //EN EL CONTROL DE TEXTO/CALENDAR!!!!
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    try {
                                        java.util.Date tempDate = sdf.parse(cd.getValor());
                                        miFecha = tempDate.getTime();
                                    } catch (java.text.ParseException pe) {
                                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        try {
                                            java.util.Date tempDate = sdf.parse(cd.getValor());
                                            miFecha = tempDate.getTime();
                                        } catch (java.text.ParseException pe2) {
                                            pe2.printStackTrace();
                                        }
                                        //ignore
                                    }
                                }
                            }
                            pstmnt.setTimestamp(i, new Timestamp(miFecha));
                        }
                        break;
                    case // String
                    10:
                        pstmnt.setString(i, cd.getValor());
                        break;
                    case // Long String
                    12:
                        throw new SQLException("Tipo de Dato (" + Util.getTipoDato(tcv_tipo) + ") no soportado en esta version (" + tcv_tipo + ")");
                    default:
                        throw new SQLException("Tipo de Dato desconocido (" + tcv_tipo + ")");
                }
            }
            pstmntFolder = conn.prepareStatement("INSERT INTO imx_carpeta " + "(titulo_aplicacion, id_gabinete, id_carpeta, nombre_carpeta, nombre_usuario, " + "bandera_raiz, fh_creacion, fh_modificacion, numero_accesos, numero_carpetas, " + "numero_documentos, descripcion, password) " + "VALUES (?,?,0,?,?,'S',?,?,0,0,0,'Módulo de Control de Gestion','-1')");
            pstmntFolder.setString(1, app.getTituloAplicacion());
            pstmntFolder.setInt(2, id_gabinete);
            pstmntFolder.setString(3, c.getFolio());
            pstmntFolder.setString(4, u_login);
            Timestamp t = new Timestamp(System.currentTimeMillis());
            pstmntFolder.setTimestamp(5, t);
            pstmntFolder.setTimestamp(6, t);
            pstmnt.executeUpdate();
            pstmntFolder.executeUpdate();
            String nombre_estructura = null;
            if (c.getIdTC() == 52) {
                nombre_estructura = "REINTEGROCONT";
            }
            if (c.getIdTC() == 15) {
                nombre_estructura = "est_rein_pres";
            }
            if (c.getIdTC() == 67) {
                nombre_estructura = "MANUALCONTABLE";
            }
            creaEstructuraCarpeta(conn, app.getTituloAplicacion(), id_gabinete, u_login, c.getFolio(), nombre_estructura);
            return id_gabinete;
        } finally {
            if (pstmntFolder != null)
                pstmntFolder.close();
            if (pstmnt != null)
                pstmnt.close();
            pstmntFolder = null;
            pstmnt = null;
        }
    }

    public static boolean delete(Connection conn, String titulo_aplicacion, int id_gabinete) throws SQLException {
        Statement stmnt = null;
        ResultSet rs = null;
        boolean retVal = false;
        try {
            stmnt = conn.createStatement();
            stmnt.addBatch("DELETE FROM imx_pagina WHERE titulo_aplicacion = '" + titulo_aplicacion + "' AND id_gabinete = " + id_gabinete);
            stmnt.addBatch("DELETE FROM imx_docto_virtual WHERE cv_aplicacion_org = '" + titulo_aplicacion + "' AND id_gabinete_org = " + id_gabinete);
            stmnt.addBatch("DELETE FROM imx_documento WHERE titulo_aplicacion = '" + titulo_aplicacion + "' AND id_gabinete = " + id_gabinete);
            stmnt.addBatch("DELETE FROM imx_org_carpeta WHERE titulo_aplicacion = '" + titulo_aplicacion + "' AND id_gabinete = " + id_gabinete);
            stmnt.addBatch("DELETE FROM imx_carpeta WHERE titulo_aplicacion = '" + titulo_aplicacion + "' AND id_gabinete = " + id_gabinete);
            stmnt.addBatch("UPDATE imx_carpeta SET numero_carpetas = numero_carpetas - 1" + " WHERE titulo_aplicacion = '" + titulo_aplicacion + "' AND id_gabinete = " + id_gabinete);
            stmnt.addBatch("DELETE FROM imx" + titulo_aplicacion + " WHERE id_gabinete = " + id_gabinete);
            stmnt.executeBatch();
            conn.commit();
            retVal = true;
        } finally {
            if (rs != null)
                rs.close();
            if (stmnt != null)
                stmnt.close();
            rs = null;
            stmnt = null;
        }
        return retVal;
    }

    private static synchronized int getNewIdGabinete(Connection conn, String titulo_aplicacion) throws SQLException {
        Statement stmnt = null;
        ResultSet rs = null;
        int id_gabinete = -1;
        try {
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery("SELECT MAX(id_gabinete) FROM imx" + titulo_aplicacion.toLowerCase());
            rs.next();
            id_gabinete = rs.getInt(1) + 1;
        } finally {
            if (rs != null)
                rs.close();
            if (stmnt != null)
                stmnt.close();
            rs = null;
            stmnt = null;
        }
        return id_gabinete;
    }

    public static String[][] getQueryByExampleAplicacionData(Connection conn, String titulo_aplicacion, Map map) throws SQLException {
        PreparedStatement pstmnt0 = null;
        PreparedStatement pstmnt1 = null;
        ResultSet rs = null;
        String[][] data = new String[0][0];
        try {
            Set qbe = map.keySet();
            Iterator qbeIter = qbe.iterator();
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            while (qbeIter.hasNext()) {
                String key = (String) qbeIter.next();
                String[] keyValues = (String[]) map.get(key);
                int tipoDato = Integer.parseInt(keyValues[0]);
                String value = keyValues[2];
                if ("".equals(value))
                    continue;
                switch(tipoDato) {
                    // Small Integer
                    case 3:
                    // Long Integer
                    case 4:
                    // Decimal
                    case 5:
                    case // Double, Float
                    7:
                        where.append(token + key + " = ?");
                        break;
                    case // Fecha
                    8:
                        DBMS dbms = new DBMS(conn);
                        String[] fecha = value.split("-");
                        switch(fecha.length) {
                            case // Año
                            1:
                                where.append(token + dbms.toChar(key, "YYYY") + " = ?");
                                break;
                            case // Mes o Año-Mes
                            2:
                                if ("".equals(fecha[0])) {
                                    // Mes
                                    where.append(token + dbms.toChar(key, "MM") + " = ?");
                                } else {
                                    // Año-Mes
                                    where.append(token + dbms.toChar(key, "YYYYMM") + " = ?");
                                }
                                break;
                            case // Dia, Mes-Dia, Año-Dia o Completo
                            3:
                                if ("".equals(fecha[0]) && "".equals(fecha[1])) {
                                    // Dia
                                    where.append(token + dbms.toChar(key, "DD") + " = ?");
                                } else if ("".equals(fecha[0])) {
                                    // Mes-Dia
                                    where.append(token + dbms.toChar(key, "MMDD") + " = ?");
                                } else if ("".equals(fecha[1])) {
                                    // Año-Dia
                                    where.append(token + dbms.toChar(key, "YYYYDD") + " = ?");
                                } else {
                                    // Completo
                                    where.append(token + dbms.toChar(key, "YYYYMMDD") + " = ?");
                                }
                                break;
                        }
                        break;
                    case // String
                    10:
                        where.append(token + key + " LIKE '" + value + "'");
                        break;
                    case // Long String
                    12:
                        // TODO Falta implementar Long String
                        break;
                }
                token = " AND ";
            }
            pstmnt0 = conn.prepareStatement("SELECT COUNT(id_gabinete) FROM imx" + titulo_aplicacion.toLowerCase() + where.toString());
            pstmnt1 = conn.prepareStatement("SELECT * FROM imx" + titulo_aplicacion.toLowerCase() + where.toString() + " ORDER BY 3");
            int colIdx = 1;
            Set keys = map.keySet();
            Iterator keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = (String) keyIter.next();
                String[] keyValues = (String[]) map.get(key);
                int tipoDato = Integer.parseInt(keyValues[0]);
                String value = keyValues[2].replaceAll("%", "");
                if ("".equals(value))
                    continue;
                switch(tipoDato) {
                    // Small Integer
                    case 3:
                    case // Long Integer
                    4:
                        pstmnt0.setInt(colIdx, Integer.parseInt(value));
                        pstmnt1.setInt(colIdx, Integer.parseInt(value));
                        break;
                    // Decimal
                    case 5:
                    case // Double, Float
                    7:
                        pstmnt0.setDouble(colIdx, Double.parseDouble(value));
                        pstmnt1.setDouble(colIdx, Double.parseDouble(value));
                        break;
                    case // String
                    10:
                        // No se asignan parametros para los tipos String
                        break;
                    case // Long String
                    12:
                        // TODO Falta implementar Long String
                        break;
                    case // Fecha
                    8:
                        String[] fecha = value.split("-");
                        switch(fecha.length) {
                            case // Año
                            1:
                                pstmnt0.setString(colIdx, fecha[0]);
                                pstmnt1.setString(colIdx, fecha[0]);
                                break;
                            case // Mes o Año-Mes
                            2:
                                if ("".equals(fecha[0])) {
                                    // Mes
                                    pstmnt0.setString(colIdx, fecha[1]);
                                    pstmnt1.setString(colIdx, fecha[1]);
                                } else {
                                    // Año-Mes
                                    pstmnt0.setString(colIdx, fecha[0] + fecha[1]);
                                    pstmnt1.setString(colIdx, fecha[0] + fecha[1]);
                                }
                                break;
                            case // Dia, Mes-Dia, Año-Dia o Completo
                            3:
                                if ("".equals(fecha[0]) && "".equals(fecha[1])) {
                                    // Dia
                                    pstmnt0.setString(colIdx, fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[2]);
                                } else if ("".equals(fecha[0])) {
                                    // Mes-Dia
                                    pstmnt0.setString(colIdx, fecha[1] + fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[1] + fecha[2]);
                                } else if ("".equals(fecha[1])) {
                                    // Año-Dia
                                    pstmnt0.setString(colIdx, fecha[0] + fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[0] + fecha[2]);
                                } else {
                                    // Completo
                                    pstmnt0.setString(colIdx, fecha[0] + fecha[1] + fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[0] + fecha[1] + fecha[2]);
                                }
                                break;
                        }
                        break;
                }
                colIdx++;
            }
            rs = pstmnt0.executeQuery();
            if (!rs.next())
                return data;
            int totRows = rs.getInt(1);
            rs = pstmnt1.executeQuery();
            int totCols = rs.getMetaData().getColumnCount() - 1;
            data = new String[totRows][totCols];
            for (int row = 0; rs.next(); row++) {
                for (int i = 0; i < totCols; i++) {
                    data[row][i] = (i == 0) ? rs.getString("id_gabinete") : rs.getString(i + 2);
                    System.out.println(data[row][i]);
                }
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt1 != null)
                pstmnt1.close();
            if (pstmnt0 != null)
                pstmnt0.close();
            rs = null;
            pstmnt1 = null;
            pstmnt0 = null;
        }
        return data;
    }

    public static String[][] getQueryByExampleAplicacionData(Connection conn, String titulo_aplicacion, String u_login, Map map) throws SQLException {
        PreparedStatement pstmnt0 = null;
        PreparedStatement pstmnt1 = null;
        ResultSet rs = null;
        String[][] data = new String[0][0];
        Cobertura c = null;
        String whereCBO;
        String fromCBO;
        try {
            // 1. Genera condicion para consulta a BD (a partir de Lista de campos y valores del criterio de seleccion)
            Set qbe = map.keySet();
            Iterator qbeIter = qbe.iterator();
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            while (qbeIter.hasNext()) {
                String key = (String) qbeIter.next();
                String[] keyValues = (String[]) map.get(key);
                int tipoDato = Integer.parseInt(keyValues[0]);
                String value = keyValues[2];
                if ("".equals(value))
                    continue;
                switch(tipoDato) {
                    // Small Integer
                    case 3:
                    // Long Integer
                    case 4:
                    // Decimal
                    case 5:
                    case // Double, Float
                    7:
                        where.append(token + key + " = ?");
                        break;
                    case // Fecha
                    8:
                        DBMS dbms = new DBMS(conn);
                        String[] fecha = value.split("-");
                        switch(fecha.length) {
                            case // Aï¿½o
                            1:
                                where.append(token + dbms.toChar(key, "YYYY") + " = ?");
                                break;
                            case // Mes o Aï¿½o-Mes
                            2:
                                if ("".equals(fecha[0])) {
                                    // Mes
                                    where.append(token + dbms.toChar(key, "MM") + " = ?");
                                } else {
                                    // Aï¿½o-Mes
                                    where.append(token + dbms.toChar(key, "YYYYMM") + " = ?");
                                }
                                break;
                            case // Dia, Mes-Dia, Aï¿½o-Dia o Completo
                            3:
                                if ("".equals(fecha[0]) && "".equals(fecha[1])) {
                                    // Dia
                                    where.append(token + dbms.toChar(key, "DD") + " = ?");
                                } else if ("".equals(fecha[0])) {
                                    // Mes-Dia
                                    where.append(token + dbms.toChar(key, "MMDD") + " = ?");
                                } else if ("".equals(fecha[1])) {
                                    // Aï¿½o-Dia
                                    where.append(token + dbms.toChar(key, "YYYYDD") + " = ?");
                                } else {
                                    // Completo
                                    where.append(token + dbms.toChar(key, "YYYYMMDD") + " = ?");
                                }
                                break;
                        }
                        break;
                    case // String
                    10:
                        where.append(token + key + " LIKE '%" + value + "%'");
                        break;
                    case // Long String
                    12:
                        // TODO Falta implementar Long String
                        break;
                }
                token = " AND ";
            }
            if (where.length() == 0)
                where.append("WHERE 1=1");
            // 2. Agrega cobertura operativa (si procede)
            if (u_login == null) {
                c = null;
            } else {
                c = CoberturaManager.selectByIdUsuario(conn, u_login, GestionInterface.CBO_CAT_AREAS, GestionInterface.CBO_PRD_GESTION, titulo_aplicacion);
            }
            if (c != null) {
                whereCBO = c.getCoWhereClause();
                fromCBO = c.getCoFromClause();
            } else {
                whereCBO = "";
                fromCBO = "";
            }
            // 3. Preparar lista de campos
            String tabla = "imx" + titulo_aplicacion.toLowerCase();
            String lstCampos = "";
            String sep = "";
            for (int i = 0; i < GestionInterface.APP_LST_CAMPOS.length; i++) {
                lstCampos += sep + tabla + "." + GestionInterface.APP_LST_CAMPOS[i][0];
                sep = ",";
            }
            if (lstCampos == "")
                lstCampos = tabla + ".*";
            // 4. Consulta la Informacion
            String query = "SELECT COUNT(DISTINCT id_gabinete) FROM " + tabla + ("".equals(fromCBO) ? "" : "," + fromCBO) + " " + where.toString() + ("".equals(whereCBO) ? "" : " " + whereCBO);
            pstmnt0 = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            query = "SELECT DISTINCT " + lstCampos + " FROM " + tabla + ("".equals(fromCBO) ? "" : "," + fromCBO) + " " + where.toString() + ("".equals(whereCBO) ? "" : " " + whereCBO) + " ORDER BY 3";
            pstmnt1 = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            //System.out.println("[AplicacionManager] u_login=" + u_login);
            //System.out.println("[AplicacionManager] " + "SELECT DISTINCT "
            //		+ lstCampos + " FROM "
            //		+ tabla + ("".equals(fromCBO) ? "" : "," + fromCBO) + " " + where.toString()
            //		+ ("".equals(whereCBO) ? "" : " " + whereCBO)
            //		+ " ORDER BY 3");
            int colIdx = 1;
            Set keys = map.keySet();
            Iterator keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = (String) keyIter.next();
                String[] keyValues = (String[]) map.get(key);
                int tipoDato = Integer.parseInt(keyValues[0]);
                String value = keyValues[2].replaceAll("%", "");
                if ("".equals(value))
                    continue;
                switch(tipoDato) {
                    // Small Integer
                    case 3:
                    case // Long Integer
                    4:
                        pstmnt0.setInt(colIdx, Integer.parseInt(value));
                        pstmnt1.setInt(colIdx, Integer.parseInt(value));
                        break;
                    // Decimal
                    case 5:
                    case // Double, Float
                    7:
                        pstmnt0.setDouble(colIdx, Double.parseDouble(value));
                        pstmnt1.setDouble(colIdx, Double.parseDouble(value));
                        break;
                    case // String
                    10:
                        // No se asignan parametros para los tipos String
                        break;
                    case // Long String
                    12:
                        // TODO Falta implementar Long String
                        break;
                    case // Fecha
                    8:
                        String[] fecha = value.split("-");
                        switch(fecha.length) {
                            case // Aï¿½o
                            1:
                                pstmnt0.setString(colIdx, fecha[0]);
                                pstmnt1.setString(colIdx, fecha[0]);
                                break;
                            case // Mes o Aï¿½o-Mes
                            2:
                                if ("".equals(fecha[0])) {
                                    // Mes
                                    pstmnt0.setString(colIdx, fecha[1]);
                                    pstmnt1.setString(colIdx, fecha[1]);
                                } else {
                                    // Aï¿½o-Mes
                                    pstmnt0.setString(colIdx, fecha[0] + fecha[1]);
                                    pstmnt1.setString(colIdx, fecha[0] + fecha[1]);
                                }
                                break;
                            case // Dia, Mes-Dia, Aï¿½o-Dia o Completo
                            3:
                                if ("".equals(fecha[0]) && "".equals(fecha[1])) {
                                    // Dia
                                    pstmnt0.setString(colIdx, fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[2]);
                                } else if ("".equals(fecha[0])) {
                                    // Mes-Dia
                                    pstmnt0.setString(colIdx, fecha[1] + fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[1] + fecha[2]);
                                } else if ("".equals(fecha[1])) {
                                    // Aï¿½o-Dia
                                    pstmnt0.setString(colIdx, fecha[0] + fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[0] + fecha[2]);
                                } else {
                                    // Completo
                                    pstmnt0.setString(colIdx, fecha[0] + fecha[1] + fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[0] + fecha[1] + fecha[2]);
                                }
                                break;
                        }
                        break;
                }
                colIdx++;
            }
            rs = pstmnt0.executeQuery();
            if (!rs.next())
                return data;
            int totRows = rs.getInt(1);
            rs = pstmnt1.executeQuery();
            // 5. Produce Resultado
            //    Supone que ID_GABINETE,ACTIVO son los 2 primeros campos del resultado
            //    Se regresa el valor de ID_GABINETE, pero ACTIVO no se regresa
            int totCols = rs.getMetaData().getColumnCount() - 1;
            data = new String[totRows][totCols];
            for (int row = 0; rs.next(); row++) {
                for (int i = 0; i < totCols; i++) {
                    data[row][i] = (i == 0) ? rs.getString("id_gabinete") : rs.getString(i + 2);
                }
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt1 != null)
                pstmnt1.close();
            if (pstmnt0 != null)
                pstmnt0.close();
            rs = null;
            pstmnt1 = null;
            pstmnt0 = null;
        }
        return data;
    }

    public static PaginaData getQueryByExampleAplicacionDataAvanzada(Connection conn, String titulo_aplicacion, String u_login, Map map, String param_fecha_de, String param_fecha_a, String tipoasunto, PaginaData param_pd) throws SQLException {
        PreparedStatement pstmnt0 = null;
        PreparedStatement pstmnt1 = null;
        ResultSet rs = null;
        String[][] data = new String[0][0];
        PaginaData pd = new PaginaData();
        java.util.Calendar calendario = new java.util.GregorianCalendar();
        int anio = calendario.get(java.util.Calendar.YEAR);
        int mes = calendario.get(java.util.Calendar.MONTH) + 1;
        int dia = calendario.get(java.util.Calendar.DAY_OF_MONTH);
        if (param_fecha_a.trim().equals("")) {
            System.out.println("Valor dentro del if:" + dia + "/" + mes + "/" + anio);
            param_fecha_a = dia + "/" + mes + "/" + anio;
        }
        if (param_fecha_de.trim().equals("")) {
            System.out.println("Valor dentro del if:" + dia + "/" + mes + "/" + anio);
            param_fecha_de = dia + "/" + mes + "/" + anio;
        }
        String[] inv_fecha_de = new String[3];
        String[] inv_fecha_a = new String[3];
        int cont = 2;
        StringTokenizer fech_de = new StringTokenizer(param_fecha_de, "/");
        while (fech_de.hasMoreTokens()) {
            inv_fecha_de[cont--] = fech_de.nextToken();
        }
        cont = 2;
        StringTokenizer fech_a = new StringTokenizer(param_fecha_a, "/");
        while (fech_a.hasMoreTokens()) {
            inv_fecha_a[cont--] = fech_a.nextToken();
        }
        String[][] APP_LST_CAMPOS = null;
        Cobertura c = null;
        String whereCBO;
        String fromCBO;
        try {
            // 1. Genera condicion para consulta a BD (a partir de Lista de campos y valores del criterio de seleccion)
            Set qbe = map.keySet();
            Iterator qbeIter = qbe.iterator();
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            while (qbeIter.hasNext()) {
                String key = (String) qbeIter.next();
                String[] keyValues = (String[]) map.get(key);
                int tipoDato = Integer.parseInt(keyValues[0]);
                String value = keyValues[2];
                if (value == null || "".equals(value)) {
                    continue;
                }
                switch(tipoDato) {
                    // Small Integer
                    case 3:
                    // Long Integer
                    case 4:
                    // Decimal
                    case 5:
                    case // Double, Float
                    7:
                        where.append(token + key + " = ?");
                        break;
                    case // Fecha
                    8:
                        DBMS dbms = new DBMS(conn);
                        String[] fecha = value.split("/");
                        switch(fecha.length) {
                            case // Año
                            1:
                                where.append(token + "dpc_f_registro BETWEEN convert(datetime," + inv_fecha_de[0] + "/" + inv_fecha_de[1] + "/" + inv_fecha_de[2] + ")AND convert(datetime,'" + inv_fecha_a[0] + "/" + inv_fecha_a[1] + "/" + inv_fecha_a[2] + "')");
                                break;
                            case // Mes o Año-Mes
                            2:
                                if ("".equals(fecha[0])) {
                                    // Mes
                                    where.append(token + dbms.toChar(key, "MM") + " = ?");
                                } else {
                                    // Año-Mes
                                    where.append(token + dbms.toChar(key, "YYYYMM") + " = ?");
                                }
                                break;
                            case // Dia, Mes-Dia, Año-Dia o Completo
                            3:
                                where.append(token + "dpc_f_registro BETWEEN convert(datetime,'" + inv_fecha_de[0] + "/" + inv_fecha_de[1] + "/" + inv_fecha_de[2] + "')AND convert(datetime,'" + inv_fecha_a[0] + "/" + inv_fecha_a[1] + "/" + inv_fecha_a[2] + "')");
                                break;
                        }
                        break;
                    case // String
                    10:
                        APP_LST_CAMPOS = (tipoasunto.equals("I") ? GestionInterface.APP_LST_CAMPOS_AVANZADA_I : GestionInterface.APP_LST_CAMPOS_AVANZADA_E);
                        for (int i = 0; i < APP_LST_CAMPOS.length; i++) {
                            if (APP_LST_CAMPOS[i][0].equals(key.toUpperCase())) {
                                where.append(token + key + (APP_LST_CAMPOS[i][1].equals("*") ? " LIKE '%" + value + "%'" : " = '" + value + "'"));
                                break;
                            }
                        }
                        break;
                    case // Long String
                    12:
                        // TODO Falta implementar Long String
                        break;
                }
                token = " AND ";
            }
            if (where.length() == 0) {
                where.append("WHERE 1=1");
            }
            // 2. Agrega cobertura operativa (si procede)
            if (u_login == null) {
                c = null;
            } else {
                c = CoberturaManager.selectByIdUsuario(conn, u_login, GestionInterface.CBO_CAT_AREAS, GestionInterface.CBO_PRD_GESTION, titulo_aplicacion);
            }
            if (c != null) {
                whereCBO = c.getCoWhereClause();
                fromCBO = c.getCoFromClause();
            } else {
                whereCBO = "";
                fromCBO = "";
            }
            // 3. Preparar lista de campos
            String tabla = "imx" + titulo_aplicacion.toLowerCase();
            String lstCampos = "";
            String sep = "";
            APP_LST_CAMPOS = (tipoasunto.equals("I") ? GestionInterface.APP_LST_CAMPOS_AVANZADA_I : GestionInterface.APP_LST_CAMPOS_AVANZADA_E);
            for (int i = 0; i < APP_LST_CAMPOS.length; i++) {
                lstCampos += sep + tabla + "." + APP_LST_CAMPOS[i][0];
                sep = ",";
            }
            if (lstCampos == "") {
                lstCampos = tabla + ".*";
            }
            String query = "SET DATEFORMAT ymd " + "SELECT COUNT(DISTINCT imx" + titulo_aplicacion.toLowerCase() + ".id_gabinete) " + " FROM " + tabla + ("".equals(fromCBO) ? "" : "," + fromCBO) + " " + where.toString() + ("".equals(whereCBO) ? "" : " " + whereCBO);
            // 4. Consulta la Informacion
            pstmnt0 = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            query = "SET DATEFORMAT ymd " + "SELECT * FROM ( " + "					SELECT *, ROW_NUMBER() OVER (ORDER BY  imxexpedientes.ID_GABINETE) AS RowNumber " + "                   FROM (" + "                        SELECT " + //GAF 2010-05-06 START
            //+ " DISTINCT "
            //GAF 2010-05-06 END
            lstCampos + //GAF 2010-05-06 START
            ", MAX(co.co_fecha_ini) as F_ENVIO " + //GAF 2010-05-06 END
            "                          FROM " + tabla + ("".equals(fromCBO) ? "" : "," + fromCBO) + " " + "                           WITH (NOLOCK)" + where.toString() + ("".equals(whereCBO) ? "" : " " + whereCBO) + //GAF 2010-05-06 START
            "  GROUP BY " + "		imxexpedientes.ID_GABINETE," + "		imxexpedientes.ACTIVO," + "		imxexpedientes.FOLIO," + "		imxexpedientes.NCONTROL," + "		imxexpedientes.REFERENCIA," + "		imxexpedientes.ALCANCE," + "		imxexpedientes.ANTECEDENTE," + "		imxexpedientes.ASUNTO," + "		imxexpedientes.TIPOASUNTO," + "		imxexpedientes.TDDESCRIPCION," + "		imxexpedientes.PRIORIDAD," + "		imxexpedientes.DPC_F_RECEPCION," + "		imxexpedientes.DPC_F_REGISTRO," + "		imxexpedientes.DPC_F_DOCUMENTO," + "		imxexpedientes.DPC_F_LIMITE," + ((tipoasunto.equals("I")) ? "		imxexpedientes.REMINUNOMBRE," + "		imxexpedientes.REMINPTONOM," + "		imxexpedientes.REMINDDESC," : "		imxexpedientes.RENOMBRE," + "		imxexpedientes.RECARGO," + "		imxexpedientes.REPROCEDENCIA," + "		imxexpedientes.REESTADO," + "		imxexpedientes.REMUNICIPIO," + "		imxexpedientes.REDIRECCION," + "		imxexpedientes.RELOCALIDAD,") + "		imxexpedientes.RESUNOMBRE," + "		imxexpedientes.RESPTONOMBRE," + "		imxexpedientes.RESDDESC" + //GAF 2010-05-06 END
            ") as imxexpedientes ) as expedientes " + "WHERE RowNumber BETWEEN " + (param_pd.getTamanoPaginas() * param_pd.getNumeroPagina() + 1) + " AND " + param_pd.getTamanoPaginas() * (param_pd.getNumeroPagina() + 1);
            System.out.println("AplicacionManager.getQueryByExampleAplicacionDataAvanzada query=[" + query + "]");
            pstmnt1 = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            System.out.println("[AplicacionManager] u_login=" + u_login);
            System.out.println("[AplicacionManager]" + query);
            int colIdx = 1;
            Set keys = map.keySet();
            Iterator keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = (String) keyIter.next();
                String[] keyValues = (String[]) map.get(key);
                int tipoDato = Integer.parseInt(keyValues[0]);
                String value = keyValues[2];
                if (value == null || "".equals(value))
                    continue;
                switch(tipoDato) {
                    // Small Integer
                    case 3:
                    case // Long Integer
                    4:
                        pstmnt0.setInt(colIdx, Integer.parseInt(value));
                        pstmnt1.setInt(colIdx, Integer.parseInt(value));
                        break;
                    // Decimal
                    case 5:
                    case // Double, Float
                    7:
                        pstmnt0.setDouble(colIdx, Double.parseDouble(value));
                        pstmnt1.setDouble(colIdx, Double.parseDouble(value));
                        break;
                    case // String
                    10:
                        // No se asignan parametros para los tipos String
                        break;
                    case // Long String
                    12:
                        // TODO Falta implementar Long String
                        break;
                    case // Fecha
                    8:
                        String[] fecha = value.split("/");
                        switch(fecha.length) {
                            case // Año
                            1:
                                pstmnt0.setString(colIdx, fecha[0]);
                                pstmnt1.setString(colIdx, fecha[0]);
                                break;
                            case // Mes o Año-Mes
                            2:
                                if ("".equals(fecha[0])) {
                                    // Mes
                                    pstmnt0.setString(colIdx, fecha[1]);
                                    pstmnt1.setString(colIdx, fecha[1]);
                                } else {
                                    // Año-Mes
                                    pstmnt0.setString(colIdx, fecha[0] + fecha[1]);
                                    pstmnt1.setString(colIdx, fecha[0] + fecha[1]);
                                }
                                break;
                            case // Dia, Mes-Dia, Año-Dia o Completo
                            3:
                                if ("".equals(fecha[0]) && "".equals(fecha[1])) {
                                    // Dia
                                    pstmnt0.setString(colIdx, fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[2]);
                                } else if ("".equals(fecha[0])) {
                                    // Mes-Dia
                                    pstmnt0.setString(colIdx, fecha[1] + fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[1] + fecha[2]);
                                } else if ("".equals(fecha[1])) {
                                    // Año-Dia
                                    pstmnt0.setString(colIdx, fecha[0] + fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[0] + fecha[2]);
                                } else {
                                    // Completo
                                }
                                break;
                        }
                        break;
                }
                colIdx++;
            }
            rs = pstmnt0.executeQuery();
            if (rs.next()) {
                int totRows = rs.getInt(1);
                pd.setNumeroRegistros(totRows);
                if (totRows > 0) {
                    rs = pstmnt1.executeQuery();
                    //GAF 2010-04-16
                    //En los manager no debe haber commits!
                    //solamente en los businesslogic o servlets
                    //conn.commit();
                    // 5. Produce Resultado
                    //    Supone que ID_GABINETE,ACTIVO son los 2 primeros campos del resultado
                    //    Se regresa el valor de ID_GABINETE, pero ACTIVO no se regresa
                    int totCols = rs.getMetaData().getColumnCount() - 1;
                    data = new String[(param_pd.getTamanoPaginas() > totRows) ? totRows : param_pd.getTamanoPaginas()][totCols];
                    for (int row = 0; rs.next(); row++) {
                        for (int i = 0; i < totCols; i++) {
                            data[row][i] = (i == 0) ? rs.getString("id_gabinete") : rs.getString(i + 2);
                        }
                    }
                }
            }
            pd.setData(data);
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt1 != null)
                pstmnt1.close();
            if (pstmnt0 != null)
                pstmnt0.close();
            rs = null;
            pstmnt1 = null;
            pstmnt0 = null;
        }
        return pd;
    }

    //Ethiel, este nos trae lo del gabinete mas los folios de los casos que no han sido guardados en gaveta
    public static String[][] getQueryByExampleAplicacionDataEnConsulta(Connection conn, String titulo_aplicacion, Map map) throws SQLException {
        PreparedStatement pstmnt0 = null;
        PreparedStatement pstmnt1 = null;
        ResultSet rs = null;
        String[][] data = new String[0][0];
        try {
            Set qbe = map.keySet();
            Iterator qbeIter = qbe.iterator();
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            while (qbeIter.hasNext()) {
                String key = (String) qbeIter.next();
                String[] keyValues = (String[]) map.get(key);
                int tipoDato = Integer.parseInt(keyValues[0]);
                String value = keyValues[2];
                if ("".equals(value))
                    continue;
                switch(tipoDato) {
                    // Small Integer
                    case 3:
                    // Long Integer
                    case 4:
                    // Decimal
                    case 5:
                    case // Double, Float
                    7:
                        where.append(token + key + " = ?");
                        break;
                    case // Fecha
                    8:
                        DBMS dbms = new DBMS(conn);
                        String[] fecha = value.split("-");
                        switch(fecha.length) {
                            case // Año
                            1:
                                where.append(token + dbms.toChar(key, "YYYY") + " = ?");
                                break;
                            case // Mes o Año-Mes
                            2:
                                if ("".equals(fecha[0])) {
                                    // Mes
                                    where.append(token + dbms.toChar(key, "MM") + " = ?");
                                } else {
                                    // Año-Mes
                                    where.append(token + dbms.toChar(key, "YYYYMM") + " = ?");
                                }
                                break;
                            case // Dia, Mes-Dia, Año-Dia o Completo
                            3:
                                if ("".equals(fecha[0]) && "".equals(fecha[1])) {
                                    // Dia
                                    where.append(token + dbms.toChar(key, "DD") + " = ?");
                                } else if ("".equals(fecha[0])) {
                                    // Mes-Dia
                                    where.append(token + dbms.toChar(key, "MMDD") + " = ?");
                                } else if ("".equals(fecha[1])) {
                                    // Año-Dia
                                    where.append(token + dbms.toChar(key, "YYYYDD") + " = ?");
                                } else {
                                    // Completo
                                    where.append(token + dbms.toChar(key, "YYYYMMDD") + " = ?");
                                }
                                break;
                            case //Ethiel, para busquedas por rango (solo funcionara si llenan la fecha completa por eso es el 6)
                            6:
                                where.append(token + dbms.toChar(key, "YYYYMMDD") + " between ? and ?");
                                break;
                        }
                        break;
                    case // String
                    10:
                        where.append(token + key + " LIKE '%" + value + "%'");
                        break;
                    case // Long String
                    12:
                        // TODO Falta implementar Long String
                        break;
                }
                token = " AND ";
            }
            //TODO: cambiar estos dos querys por uno que nos de los registros que tengan caso y que ademas esten en consulta
            //Primer Quey
            String Query0 = "SELECT COUNT(id_gabinete) FROM imx" + titulo_aplicacion.toLowerCase();
            String Queryfolio = "FOLIO in ";
            Queryfolio += " (select C_FOLIO from cg_caso where ID_TC=(select ID_TC from CG_TIPO_CASO where TC_GAVETA_ASOCIADA='" + titulo_aplicacion.toLowerCase() + "' ) ";
            Queryfolio += " and ID_CASO in (select ID_CASO from CG_CASO_OPERACION (NOLOCK) where ID_OPER=(select id_oper from CG_OPERACION (NOLOCK) where ID_TC=(select ID_TC from CG_TIPO_CASO (NOLOCK) where TC_GAVETA_ASOCIADA='" + titulo_aplicacion.toLowerCase() + "') and O_NOMBRE like '%consulta%'))) ";
            if (!where.equals("")) {
                Query0 += " WHERE ";
                Query0 += Queryfolio;
            } else {
                Query0 += where.toString();
                Query0 += " AND ";
                Query0 += Queryfolio;
            }
            System.out.println(Query0);
            pstmnt0 = conn.prepareStatement(Query0);
            //Segundo Quey
            String Query1 = "SELECT * FROM imx" + titulo_aplicacion.toLowerCase();
            if (!where.equals("")) {
                Query1 += " WHERE " + Queryfolio;
            } else {
                Query1 += where.toString();
                Query1 += " AND " + Queryfolio;
            }
            System.out.println(Query1);
            pstmnt1 = conn.prepareStatement(Query1 + " ORDER BY 3");
            //			FOLIO in
            //			(
            //			select C_FOLIO from cg_caso where ID_TC=(select ID_TC from CG_TIPO_CASO where TC_GAVETA_ASOCIADA='ADECUACION' )
            //			and ID_CASO in (select ID_CASO from CG_CASO_OPERACION where ID_OPER=(select id_oper from CG_OPERACION where ID_TC=(select ID_TC from CG_TIPO_CASO where TC_GAVETA_ASOCIADA='ADECUACION') and O_NOMBRE like '%consulta%'))
            //			)
            int colIdx = 1;
            Set keys = map.keySet();
            Iterator keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = (String) keyIter.next();
                String[] keyValues = (String[]) map.get(key);
                int tipoDato = Integer.parseInt(keyValues[0]);
                String value = keyValues[2].replaceAll("%", "");
                if ("".equals(value))
                    continue;
                switch(tipoDato) {
                    // Small Integer
                    case 3:
                    case // Long Integer
                    4:
                        pstmnt0.setInt(colIdx, Integer.parseInt(value));
                        pstmnt1.setInt(colIdx, Integer.parseInt(value));
                        break;
                    // Decimal
                    case 5:
                    case // Double, Float
                    7:
                        pstmnt0.setDouble(colIdx, Double.parseDouble(value));
                        pstmnt1.setDouble(colIdx, Double.parseDouble(value));
                        break;
                    case // String
                    10:
                        // No se asignan parametros para los tipos String
                        colIdx--;
                        break;
                    case // Long String
                    12:
                        // TODO Falta implementar Long String
                        break;
                    case // Fecha
                    8:
                        String[] fecha = value.split("-");
                        switch(fecha.length) {
                            case // Año
                            1:
                                pstmnt0.setString(colIdx, fecha[0]);
                                pstmnt1.setString(colIdx, fecha[0]);
                                break;
                            case // Mes o Año-Mes
                            2:
                                if ("".equals(fecha[0])) {
                                    // Mes
                                    pstmnt0.setString(colIdx, fecha[1]);
                                    pstmnt1.setString(colIdx, fecha[1]);
                                } else {
                                    // Año-Mes
                                    pstmnt0.setString(colIdx, fecha[0] + fecha[1]);
                                    pstmnt1.setString(colIdx, fecha[0] + fecha[1]);
                                }
                                break;
                            case // Dia, Mes-Dia, Año-Dia o Completo
                            3:
                                if ("".equals(fecha[0]) && "".equals(fecha[1])) {
                                    // Dia
                                    pstmnt0.setString(colIdx, fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[2]);
                                } else if ("".equals(fecha[0])) {
                                    // Mes-Dia
                                    pstmnt0.setString(colIdx, fecha[1] + fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[1] + fecha[2]);
                                } else if ("".equals(fecha[1])) {
                                    // Año-Dia
                                    pstmnt0.setString(colIdx, fecha[0] + fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[0] + fecha[2]);
                                } else {
                                    // Completo
                                    pstmnt0.setString(colIdx, fecha[0] + fecha[1] + fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[0] + fecha[1] + fecha[2]);
                                }
                                break;
                            case 6:
                                pstmnt0.setString(colIdx, fecha[0] + fecha[1] + fecha[2]);
                                pstmnt0.setString(colIdx + 1, fecha[3] + fecha[4] + fecha[5]);
                                pstmnt1.setString(colIdx, fecha[0] + fecha[1] + fecha[2]);
                                pstmnt1.setString(colIdx + 1, fecha[3] + fecha[4] + fecha[5]);
                                colIdx++;
                                break;
                        }
                        break;
                }
                colIdx++;
            }
            rs = pstmnt0.executeQuery();
            if (!rs.next())
                return data;
            int totRows = rs.getInt(1);
            rs = pstmnt1.executeQuery();
            int totCols = rs.getMetaData().getColumnCount() - 1;
            data = new String[totRows][totCols];
            for (int row = 0; rs.next(); row++) {
                for (int i = 0; i < totCols; i++) {
                    data[row][i] = (i == 0) ? rs.getString("id_gabinete") : rs.getString(i + 2);
                }
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt1 != null)
                pstmnt1.close();
            if (pstmnt0 != null)
                pstmnt0.close();
            rs = null;
            pstmnt1 = null;
            pstmnt0 = null;
        }
        return data;
    }

    //Ethiel, este nos trae lo del gabinete mas los folios de los casos que no han sido guardados en gaveta
    public static String[][] getQueryByExampleAplicacionDataYCasosIniciados(Connection conn, String titulo_aplicacion, Map map) throws SQLException {
        PreparedStatement pstmnt0 = null;
        PreparedStatement pstmnt1 = null;
        ResultSet rs = null;
        String[][] data = new String[0][0];
        try {
            Set qbe = map.keySet();
            Iterator qbeIter = qbe.iterator();
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            while (qbeIter.hasNext()) {
                String key = (String) qbeIter.next();
                String[] keyValues = (String[]) map.get(key);
                int tipoDato = Integer.parseInt(keyValues[0]);
                String value = keyValues[2];
                if ("".equals(value))
                    continue;
                switch(tipoDato) {
                    // Small Integer
                    case 3:
                    // Long Integer
                    case 4:
                    // Decimal
                    case 5:
                    case // Double, Float
                    7:
                        where.append(token + key + " = ?");
                        break;
                    case // Fecha
                    8:
                        DBMS dbms = new DBMS(conn);
                        String[] fecha = value.split("-");
                        switch(fecha.length) {
                            case // Año
                            1:
                                where.append(token + dbms.toChar(key, "YYYY") + " = ?");
                                break;
                            case // Mes o Año-Mes
                            2:
                                if ("".equals(fecha[0])) {
                                    // Mes
                                    where.append(token + dbms.toChar(key, "MM") + " = ?");
                                } else {
                                    // Año-Mes
                                    where.append(token + dbms.toChar(key, "YYYYMM") + " = ?");
                                }
                                break;
                            case // Dia, Mes-Dia, Año-Dia o Completo
                            3:
                                if ("".equals(fecha[0]) && "".equals(fecha[1])) {
                                    // Dia
                                    where.append(token + dbms.toChar(key, "DD") + " = ?");
                                } else if ("".equals(fecha[0])) {
                                    // Mes-Dia
                                    where.append(token + dbms.toChar(key, "MMDD") + " = ?");
                                } else if ("".equals(fecha[1])) {
                                    // Año-Dia
                                    where.append(token + dbms.toChar(key, "YYYYDD") + " = ?");
                                } else {
                                    // Completo
                                    where.append(token + dbms.toChar(key, "YYYYMMDD") + " = ?");
                                }
                                break;
                            case //Ethiel, para busquedas por rango (solo funcionara si llenan la fecha completa por eso es el 6)
                            6:
                                where.append(token + dbms.toChar(key, "YYYYMMDD") + " between ? and ?");
                                break;
                        }
                        break;
                    case // String
                    10:
                        where.append(token + key + " LIKE '%" + value + "%'");
                        break;
                    case // Long String
                    12:
                        // TODO Falta implementar Long String
                        break;
                }
                token = " AND ";
            }
            pstmnt0 = conn.prepareStatement("select " + " (select count(id_gabinete) from imx" + titulo_aplicacion.toLowerCase() + where.toString() + ")" + " + (select count(c_folio) from  cg_caso where c_id_gabinete=-1 and id_tc=(select id_tc from cg_tipo_caso where tc_gaveta_asociada='" + titulo_aplicacion + "'))");
            pstmnt1 = conn.prepareStatement("SELECT * FROM imx" + titulo_aplicacion.toLowerCase() + where.toString() + //Ethiel, vasconia quiere ver los casos que aun no han sigo guardados en gaveta
            //Para cada gaveta copiar el union de abajo con el numero de nulls al final dependiendo de la gaveta que se trate
            (("anp".equals(titulo_aplicacion.toLowerCase()) && "".equals(where.toString()) || where.toString().contains("casos_iniciados")) ? " union select c_id_gabinete,'N',c_folio as folio,str(id_caso) as gerencia_ventas,null as categoria,null as fecha_solicitud,null as fecha_alta,null as sku,null as estimacion_prof,null as estimacion_comp,null as estimacion_clie,null as estimacion_venta,null as nombre_producto from cg_caso WHERE c_id_gabinete=-1 and id_tc=1 " : "") + //Ethiel aqui agregar la gaveta del nuevo flujo
            " ORDER BY 3");
            int colIdx = 1;
            Set keys = map.keySet();
            Iterator keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = (String) keyIter.next();
                String[] keyValues = (String[]) map.get(key);
                int tipoDato = Integer.parseInt(keyValues[0]);
                String value = keyValues[2].replaceAll("%", "");
                if ("".equals(value))
                    continue;
                switch(tipoDato) {
                    // Small Integer
                    case 3:
                    case // Long Integer
                    4:
                        pstmnt0.setInt(colIdx, Integer.parseInt(value));
                        pstmnt1.setInt(colIdx, Integer.parseInt(value));
                        break;
                    // Decimal
                    case 5:
                    case // Double, Float
                    7:
                        pstmnt0.setDouble(colIdx, Double.parseDouble(value));
                        pstmnt1.setDouble(colIdx, Double.parseDouble(value));
                        break;
                    case // String
                    10:
                        // No se asignan parametros para los tipos String
                        colIdx--;
                        break;
                    case // Long String
                    12:
                        // TODO Falta implementar Long String
                        break;
                    case // Fecha
                    8:
                        String[] fecha = value.split("-");
                        switch(fecha.length) {
                            case // Año
                            1:
                                pstmnt0.setString(colIdx, fecha[0]);
                                pstmnt1.setString(colIdx, fecha[0]);
                                break;
                            case // Mes o Año-Mes
                            2:
                                if ("".equals(fecha[0])) {
                                    // Mes
                                    pstmnt0.setString(colIdx, fecha[1]);
                                    pstmnt1.setString(colIdx, fecha[1]);
                                } else {
                                    // Año-Mes
                                    pstmnt0.setString(colIdx, fecha[0] + fecha[1]);
                                    pstmnt1.setString(colIdx, fecha[0] + fecha[1]);
                                }
                                break;
                            case // Dia, Mes-Dia, Año-Dia o Completo
                            3:
                                if ("".equals(fecha[0]) && "".equals(fecha[1])) {
                                    // Dia
                                    pstmnt0.setString(colIdx, fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[2]);
                                } else if ("".equals(fecha[0])) {
                                    // Mes-Dia
                                    pstmnt0.setString(colIdx, fecha[1] + fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[1] + fecha[2]);
                                } else if ("".equals(fecha[1])) {
                                    // Año-Dia
                                    pstmnt0.setString(colIdx, fecha[0] + fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[0] + fecha[2]);
                                } else {
                                    // Completo
                                    pstmnt0.setString(colIdx, fecha[0] + fecha[1] + fecha[2]);
                                    pstmnt1.setString(colIdx, fecha[0] + fecha[1] + fecha[2]);
                                }
                                break;
                            case 6:
                                pstmnt0.setString(colIdx, fecha[0] + fecha[1] + fecha[2]);
                                pstmnt0.setString(colIdx + 1, fecha[3] + fecha[4] + fecha[5]);
                                pstmnt1.setString(colIdx, fecha[0] + fecha[1] + fecha[2]);
                                pstmnt1.setString(colIdx + 1, fecha[3] + fecha[4] + fecha[5]);
                                colIdx++;
                                break;
                        }
                        break;
                }
                colIdx++;
            }
            rs = pstmnt0.executeQuery();
            if (!rs.next())
                return data;
            int totRows = rs.getInt(1);
            rs = pstmnt1.executeQuery();
            int totCols = rs.getMetaData().getColumnCount() - 1;
            data = new String[totRows][totCols];
            for (int row = 0; rs.next(); row++) {
                for (int i = 0; i < totCols; i++) {
                    data[row][i] = (i == 0) ? rs.getString("id_gabinete") : rs.getString(i + 2);
                }
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt1 != null)
                pstmnt1.close();
            if (pstmnt0 != null)
                pstmnt0.close();
            rs = null;
            pstmnt1 = null;
            pstmnt0 = null;
        }
        return data;
    }

    public static synchronized String[] insert(Connection conn, String titulo_aplicacion, String nombre_usuario, String nombre_carpeta_raiz, Map map) throws SQLException {
        PreparedStatement pstmnt0 = null;
        PreparedStatement pstmnt1 = null;
        ResultSet rs = null;
        StringBuffer fldsList = null;
        String[] retVal = new String[2];
        try {
            int id_gabinete = getNewIdGabinete(conn, titulo_aplicacion);
            if (id_gabinete == -1)
                throw new SQLException("No se logró obtener un nuevo Id de Gabinete");
            retVal[0] = String.valueOf(id_gabinete);
            Set fields = map.keySet();
            Iterator fldsIter = fields.iterator();
            boolean idxValidate = false;
            String idxToken = "";
            String token = " WHERE ";
            StringBuffer sqlSelect = new StringBuffer("SELECT 1 FROM imx" + titulo_aplicacion.toLowerCase());
            StringBuffer sqlInsert = new StringBuffer("INSERT INTO imx" + titulo_aplicacion.toLowerCase() + " (id_gabinete, activo");
            StringBuffer values = new StringBuffer(" VALUES (" + id_gabinete + ", 'N'");
            while (fldsIter.hasNext()) {
                String field = (String) fldsIter.next();
                String[] fldsData = (String[]) map.get(field);
                int idxTipo = Integer.parseInt(fldsData[1]);
                String fldName = fldsData[3];
                if (idxTipo == 2) {
                    if (!idxValidate)
                        fldsList = new StringBuffer();
                    idxValidate = true;
                    fldsList.append(idxToken + fldName);
                    idxToken = "|";
                    sqlSelect.append(token + field + " = ?");
                    token = " AND ";
                }
                sqlInsert.append(", " + field);
                values.append(", ?");
            }
            sqlInsert.append(")");
            values.append(")");
            sqlInsert.append(values);
            pstmnt0 = conn.prepareStatement(sqlInsert.toString());
            pstmnt1 = conn.prepareStatement(sqlSelect.toString());
            int colSelect = 1;
            int colInsert = 1;
            Set keys = map.keySet();
            Iterator keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = (String) keyIter.next();
                String[] keyValues = (String[]) map.get(key);
                int tipoDato = Integer.parseInt(keyValues[0]);
                int idxTipo = Integer.parseInt(keyValues[1]);
                String value = keyValues[2];
                switch(tipoDato) {
                    // Small Integer
                    case 3:
                    case // Long Integer
                    4:
                        pstmnt0.setInt(colInsert++, Integer.parseInt(value));
                        if (idxValidate && (idxTipo == 2))
                            pstmnt1.setInt(colSelect++, Integer.parseInt(value));
                        break;
                    // Decimal
                    case 5:
                    case // Double, Float
                    7:
                        pstmnt0.setDouble(colInsert++, Double.parseDouble(value));
                        if (idxValidate && (idxTipo == 2))
                            pstmnt1.setDouble(colSelect++, Double.parseDouble(value));
                        break;
                    case // String
                    10:
                        pstmnt0.setString(colInsert++, value);
                        if (idxValidate && (idxTipo == 2))
                            pstmnt1.setString(colSelect++, value);
                        break;
                    case // Long String
                    12:
                        // TODO Falta implementar Long String
                        break;
                    case // Fecha
                    8:
                        pstmnt0.setDate(colInsert++, Date.valueOf(value));
                        if (idxValidate && (idxTipo == 2))
                            pstmnt1.setDate(colSelect++, Date.valueOf(value));
                        break;
                }
            }
            retVal[1] = (fldsList == null) ? null : fldsList.toString();
            if (idxValidate) {
                rs = pstmnt1.executeQuery();
                if (rs.next())
                    return retVal;
            }
            pstmnt0.executeUpdate();
            creaEstructuraCarpeta(conn, titulo_aplicacion, id_gabinete, nombre_usuario, nombre_carpeta_raiz);
            retVal[1] = null;
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt1 != null)
                pstmnt1.close();
            if (pstmnt0 != null)
                pstmnt0.close();
            rs = null;
            pstmnt1 = null;
            pstmnt0 = null;
        }
        return retVal;
    }

    public static Map<String, Aplicacion> select(Connection conn) throws SQLException {
        Map<String, Aplicacion> rm = new LinkedHashMap<String, Aplicacion>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM imx_aplicacion WHERE titulo_aplicacion != 'USR_GRALES'");
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Aplicacion app = new Aplicacion();
                app.setTableAplicacion(rs.getString("tbl_aplicacion"));
                app.setTituloAplicacion(rs.getString("titulo_aplicacion"));
                app.setDescripcion(rs.getString("descripcion"));
                app.setCamposDescripcion(DescripcionManager.select(conn, app.getTituloAplicacion()));
                rm.put(app.getTituloAplicacion(), app);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return rm;
    }

    public static Aplicacion select(Connection conn, String titulo_aplicacion) throws SQLException {
        Aplicacion rapp = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM imx_aplicacion WHERE titulo_aplicacion = ?");
            pstmnt.setString(1, titulo_aplicacion);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                rapp = new Aplicacion();
                rapp.setTableAplicacion(rs.getString("tbl_aplicacion"));
                rapp.setTituloAplicacion(rs.getString("titulo_aplicacion"));
                rapp.setDescripcion(rs.getString("descripcion"));
                rapp.setCamposDescripcion(DescripcionManager.select(conn, titulo_aplicacion));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return rapp;
    }

    public static DatosCaso selectDatosCaso(String idCaso) throws SQLException {
        DatosCaso dc = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        Connection conn = null;
        CasoOperacionBusinessLogic cobl = new CasoOperacionBusinessLogic("jdbc/gestion");
        try {
            conn = cobl.getConnection();
            query.append(" SELECT  ");
            query.append(" cIdConsolidado as folioConsolidado,  ");
            query.append(" v.nFolioPreCompromiso as folioPrecompromiso, ");
            query.append(" cDescripcion as descripcion, ");
            query.append(" CONVERT(varchar, v.fechaFin,103) as fechaFin ");
            query.append(" FROM CG_CASO c, mPrecompromisoVigencia v, mConsolidado mc ");
            query.append(" WHERE  ");
            query.append(" 	c.C_FOLIO = v.nFolioPreCompromiso ");
            query.append(" 	AND c.C_FOLIO = mc.C_FOLIO_PRE ");
            query.append(" 	AND v.aprobada = 0 ");
            query.append(" 	AND c.ID_CASO = ? ");
            query.append(" union ");
            query.append(" select cidprocedimiento as folioConsolidado, v.nfolioPrecompromiso as folioPrecompromiso, cDescripcion as descripcion, ");
            query.append(" CONVERT(varchar, v.fechaFin,103) as fechaFin  from   	CG_CASO c, mPrecompromisoVigencia v, mProcedimiento p ");
            query.append(" WHERE c.C_FOLIO = v.nFolioPreCompromiso  	AND c.C_FOLIO = p.C_FOLIO_PRE 	AND v.aprobada = 0 	AND c.ID_CASO = ?");
            query.append("  ");
            query.append("  ");
            query.append("  ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, idCaso);
            ps.setString(2, idCaso);
            rs = ps.executeQuery();
            if (rs.next()) {
                dc = new DatosCaso();
                dc.setFolioConsolidado(rs.getString("folioConsolidado"));
                dc.setFolioPrecompromiso(rs.getString("folioPrecompromiso"));
                dc.setDescripcion(rs.getString("descripcion"));
                dc.setFechaFin(rs.getString("fechaFin"));
            } else {
                dc = new DatosCaso();
                dc.setFolioConsolidado("");
                dc.setFolioPrecompromiso("");
                dc.setDescripcion("");
                dc.setFechaFin("");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (ps != null)
                ps.close();
            if (conn != null)
                conn.close();
            rs = null;
            ps = null;
            conn = null;
        }
        return dc;
    }

    public static String[] selectRutaArchivo(Connection conn, String idCaso) throws SQLException {
        String[] resultado = new String[2];
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        try {
            query.append(" SELECT ");
            query.append(" v.UNIDAD_DISCO + v.RUTA_BASE + v.RUTA_DIRECTORIO + p.NOM_ARCHIVO_ORG as ruta_archivo_org, ");
            query.append(" v.UNIDAD_DISCO + v.RUTA_BASE + v.RUTA_DIRECTORIO + p.NOM_ARCHIVO_VOL as ruta_archivo_vol ");
            query.append(" FROM IMX_PAGINA p, IMX_VOLUMEN v, IMXPRECOMPROMISO pre, CG_CASO c , mPrecompromisoVigencia vig WITH (NOLOCK) WHERE ");
            query.append(" pre.ID_GABINETE = p.ID_GABINETE ");
            query.append(" AND pre.ID_GABINETE = c.C_ID_GABINETE ");
            query.append(" AND vig.nFolioPreCompromiso = c.C_FOLIO ");
            query.append(" AND p.VOLUMEN = v.VOLUMEN ");
            query.append(" AND c.ID_CASO = ? ");
            query.append(" AND vig.aprobada = 0 ");
            query.append(" AND p.TITULO_APLICACION = 'PRECOMPROMISO' ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, idCaso);
            rs = ps.executeQuery();
            if (rs.next()) {
                resultado[0] = rs.getString("ruta_archivo_org");
                resultado[1] = rs.getString("ruta_archivo_vol");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (ps != null)
                ps.close();
            rs = null;
            ps = null;
        }
        return resultado;
    }

    public static String[] selectRutaArchivoPreComp(Connection conn, String idCaso, int idGabinete) throws SQLException {
        String[] resultado = new String[2];
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        try {
            query.append(" SELECT ");
            query.append(" v.UNIDAD_DISCO + v.RUTA_BASE + v.RUTA_DIRECTORIO + p.NOM_ARCHIVO_ORG as ruta_archivo_org, ");
            query.append(" v.UNIDAD_DISCO + v.RUTA_BASE + v.RUTA_DIRECTORIO + p.NOM_ARCHIVO_VOL as ruta_archivo_vol ");
            query.append(" FROM IMX_PAGINA p, IMX_VOLUMEN v, IMXPRECOMMATERIALES pre, CG_CASO c WITH (NOLOCK) WHERE ");
            query.append(" pre.ID_GABINETE = p.ID_GABINETE ");
            query.append(" AND p.VOLUMEN = v.VOLUMEN ");
            query.append(" AND c.ID_CASO = ? ");
            query.append(" AND p.TITULO_APLICACION = 'PRECOMMATERIALES'  ");
            query.append(" AND PRE.ID_GABINETE=C.C_ID_GABINETE  ");
            query.append(" AND PRE.ID_GABINETE=?  ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, idCaso);
            ps.setInt(2, idGabinete);
            rs = ps.executeQuery();
            if (rs.next()) {
                resultado[0] = rs.getString("ruta_archivo_org");
                resultado[1] = rs.getString("ruta_archivo_vol");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (ps != null)
                ps.close();
            rs = null;
            ps = null;
        }
        return resultado;
    }

    public static void setIdOperacionVigencia(Connection conn, String folio, int oper, String responsable) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("update cg_caso_operacion set id_oper = ?, co_responsable=?  where id_caso in(select id_caso from cg_caso where c_folio=?)");
            ps.setInt(1, oper);
            ps.setString(2, responsable);
            ps.setString(3, folio);
            ps.execute();
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        } finally {
            if (ps != null)
                ps.close();
            ps = null;
        }
    }

    public static void setIdOperacionVigenciaPrecompromiso(Connection conn, String folio, int oper, String responsable) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("update cg_caso_operacion set id_oper = ?, co_responsable=?  where id_caso in(select id_caso from cg_caso where c_folio=?)");
            ps.setInt(1, oper);
            ps.setString(2, responsable);
            ps.setString(3, folio);
            ps.execute();
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        } finally {
            if (ps != null)
                ps.close();
            ps = null;
        }
    }

    public static boolean update(Connection conn, String titulo_aplicacion, int id_gabinete, Map map) throws SQLException {
        PreparedStatement pstmnt = null;
        boolean retVal = false;
        try {
            Set fields = map.keySet();
            Iterator fldsIter = fields.iterator();
            String token = "";
            StringBuffer sqlUpdate = new StringBuffer("UPDATE imx" + titulo_aplicacion + " SET ");
            while (fldsIter.hasNext()) {
                sqlUpdate.append(token + (String) fldsIter.next() + " = ?");
                token = ", ";
            }
            sqlUpdate.append(" WHERE id_gabinete = " + id_gabinete);
            pstmnt = conn.prepareStatement(sqlUpdate.toString());
            int colIdx = 1;
            Set keys = map.keySet();
            Iterator keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = (String) keyIter.next();
                String[] keyValues = (String[]) map.get(key);
                int tipoDato = Integer.parseInt(keyValues[0]);
                String value = keyValues[2];
                if ("".equals(value)) {
                    pstmnt.setNull(colIdx, Types.NULL);
                    colIdx++;
                    continue;
                }
                switch(tipoDato) {
                    // Small Integer
                    case 3:
                    case // Long Integer
                    4:
                        pstmnt.setInt(colIdx, Integer.parseInt(value));
                        break;
                    // Decimal
                    case 5:
                    case // Double, Float
                    7:
                        pstmnt.setDouble(colIdx, Double.parseDouble(value));
                        break;
                    case // String
                    10:
                        pstmnt.setString(colIdx, value);
                        break;
                    case // Long String
                    12:
                        // TODO Falta implementar Long String
                        break;
                    case // Fecha
                    8:
                        if ("--".equals(value))
                            pstmnt.setNull(colIdx, Types.NULL);
                        else
                            pstmnt.setDate(colIdx, Date.valueOf(value));
                        break;
                }
                colIdx++;
            }
            pstmnt.executeUpdate();
            retVal = true;
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retVal;
    }

    public static void updateExpediente(Connection conn, String titulo_aplicacion, int id_gabinete, Map data) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            Map<String, String> updData = new Hashtable<String, String>();
            Aplicacion app = AplicacionManager.select(conn, titulo_aplicacion);
            pstmnt = conn.prepareStatement("SELECT * FROM " + app.getTableAplicacion() + " WHERE id_gabinete = ?");
            pstmnt.setInt(1, id_gabinete);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                for (Iterator iter = data.keySet().iterator(); iter.hasNext(); ) {
                    String name = (String) iter.next();
                    String value = (String) data.get(name);
                    //Ethiel .getBytes("ISO-8859-1"),"UTF-8"   se quita para evitar problema de enies en fortimax
                    value = new String(value);
                    if (app.getCamposDescripcion().containsKey(name)) {
                        String appVal = rs.getString(name);
                        if ((value != null) && !"".equals(value) && !value.equals(appVal))
                            updData.put(name, value);
                    }
                }
                updateExpedienteData(conn, app, id_gabinete, updData);
            }
        } finally //catch(UnsupportedEncodingException e){
        //	e.printStackTrace();
        //	throw new SQLException(e.getMessage());
        //}
        {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
    }

    private static void updateExpedienteData(Connection conn, Aplicacion app, int id_gabinete, Map<String, String> data) throws SQLException {
        PreparedStatement pstmnt = null;
        try {
            StringBuffer updList = new StringBuffer();
            for (Iterator<String> iter = data.keySet().iterator(); iter.hasNext(); ) {
                updList.append(", " + iter.next() + " = ?");
            }
            pstmnt = conn.prepareStatement("UPDATE " + app.getTableAplicacion() + " SET activo = activo" + updList.toString() + " WHERE id_gabinete = ?");
            int i = 0;
            for (Iterator<String> iter = data.keySet().iterator(); iter.hasNext(); ) {
                String name = iter.next();
                String value = data.get(name);
                Descripcion d = app.getCamposDescripcion(name);
                int tipo_dato = d.getIdTipoDatos();
                ++i;
                switch(tipo_dato) {
                    // Small Integer
                    case 3:
                    case // Long Integer
                    4:
                        pstmnt.setInt(i, Integer.parseInt(value));
                        break;
                    // Decimal
                    case 5:
                    case // Double, Float
                    7:
                        pstmnt.setLong(i, Long.parseLong(value));
                        break;
                    case // Date
                    8:
                        if ("sysdate".equalsIgnoreCase(value))
                            pstmnt.setTimestamp(i, new Timestamp(System.currentTimeMillis()));
                        else {
                            long miFecha = -1;
                            try {
                                miFecha = Long.parseLong(value);
                            } catch (NumberFormatException nfe) {
                                //quiere decir que es una fecha?
                                if (value.length() == 10) {
                                    //OJO: ESTO DEBE DE VERIFICAR EL FORMATO DEFINIDO
                                    //EN EL CONTROL DE TEXTO/CALENDAR!!!!
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    try {
                                        java.util.Date tempDate = sdf.parse(value);
                                        miFecha = tempDate.getTime();
                                    } catch (java.text.ParseException pe) {
                                        //ignore
                                    }
                                }
                            }
                            pstmnt.setTimestamp(i, new Timestamp(miFecha));
                        }
                        break;
                    case // String
                    10:
                        pstmnt.setString(i, value);
                        break;
                    case // Long String
                    12:
                        throw new SQLException("Tipo de Dato (" + Util.getTipoDato(tipo_dato) + ") no soportado en esta version (" + tipo_dato + ")");
                    default:
                        throw new SQLException("Tipo de Dato desconocido (" + tipo_dato + ")");
                }
            }
            pstmnt.setInt(++i, id_gabinete);
            pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
    }
}
