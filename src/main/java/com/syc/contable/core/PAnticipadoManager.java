package com.syc.contable.core;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.jfree.util.Log;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.servlet.GestionInterface;

public class PAnticipadoManager {

    public PAnticipadoManager() {
        super();
    }

    public static void updateFechaAct(int folio, Connection conn) throws SQLException {
        PreparedStatement pstmntUp = null;
        Calendar cal = Calendar.getInstance();
        Timestamp fecha = new Timestamp(cal.getTimeInMillis());
        try {
            pstmntUp = conn.prepareStatement("UPDATE tPAnticipadoEncanbezado SET fUltimaActualizacion = ? WHERE id_caso = ? ");
            pstmntUp.setTimestamp(1, fecha);
            pstmntUp.setInt(2, folio);
            pstmntUp.execute();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmntUp != null)
                pstmntUp.close();
            pstmntUp = null;
        }
    }

    public static void updateFechaSol(int folio, Connection conn) throws SQLException {
        PreparedStatement pstmntUp = null;
        Calendar cal = Calendar.getInstance();
        Date fecha = new Date(cal.getTimeInMillis());
        Timestamp fechatime = new Timestamp(cal.getTimeInMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            pstmntUp = conn.prepareStatement("UPDATE CG_CASO_DATO SET CD_VALOR = ? WHERE ID_CASO = ? AND ID_CD = 2 AND ID_TC = 29");
            pstmntUp.setString(1, formatter.format(fecha));
            pstmntUp.setInt(2, folio);
            pstmntUp.execute();
            pstmntUp = conn.prepareStatement("UPDATE tPAnticipadoEncanbezado SET fSolicitud = ? WHERE id_caso = ? ");
            pstmntUp.setTimestamp(1, fechatime);
            pstmntUp.setInt(2, folio);
            pstmntUp.execute();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmntUp != null)
                pstmntUp.close();
            pstmntUp = null;
        }
    }

    public static Date getFecha(int folio, Connection conn, Integer indice) throws SQLException {
        ResultSet rs = null;
        Date date = null;
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("SELECT fUltimaActualizacion,fAplicacion,fCancelacion,fSolicitud FROM tPAnticipadoEncanbezado WHERE nFolioPagoAnticipado = ?");
            pstm.setInt(1, folio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                date = rs.getDate(indice);
            }
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return date;
    }

    public static void CierreMensual(Connection conn) throws SQLException {
        PreparedStatement pstmntUp = null;
        try {
            pstmntUp = conn.prepareStatement("UPDATE CG_USUARIO_GRUPO SET G_NOMBRE = 'xCAPTURISTA_P_ANTICIPADOx' WHERE G_NOMBRE = 'CAPTURISTA_P_ANTICIPADO'");
            pstmntUp.execute();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmntUp != null)
                pstmntUp.close();
            pstmntUp = null;
        }
    }

    public static void AperturaMensual(Connection conn) throws SQLException {
        PreparedStatement pstmntUp = null;
        try {
            pstmntUp = conn.prepareStatement("UPDATE CG_USUARIO_GRUPO SET G_NOMBRE = 'CAPTURISTA_P_ANTICIPADO' WHERE G_NOMBRE = 'xCAPTURISTA_P_ANTICIPADOx'");
            pstmntUp.execute();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmntUp != null)
                pstmntUp.close();
            pstmntUp = null;
        }
    }

    public static void updateEstatus(int folio, int estatus, Connection conn) throws SQLException {
        PreparedStatement pstmntUp = null;
        Calendar cal = Calendar.getInstance();
        Date fecha = new Date(cal.getTimeInMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            pstmntUp = conn.prepareStatement("UPDATE CG_CASO_DATO SET CD_VALOR = ? WHERE ID_CASO = ? AND ID_CD = 2 AND ID_TC = 29");
            pstmntUp.setString(1, formatter.format(fecha));
            pstmntUp.setInt(2, folio);
            pstmntUp.execute();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmntUp != null)
                pstmntUp.close();
            pstmntUp = null;
        }
    }

    public static String CancelacionMasiva(Connection conn, String usuario, HttpServletRequest request) {
        PreparedStatement pstmntUp = null;
        ResultSet rs = null;
        Boolean todoBien;
        String mensaje = "";
        try {
            pstmntUp = conn.prepareStatement("" + "SELECT " + "CGCO.ID_CASO id_caso,CGCO.ID_CASO_OPER id_caso_oper,TPA.nFolioPoliza " + "FROM " + "CG_CASO_OPERACION CGCO " + "INNER JOIN " + "CG_CASO CGC " + "ON CGCO.ID_CASO = CGC.ID_CASO AND CGCO.ID_TC = CGC.ID_TC " + "INNER JOIN " + "tPAnticipadoEncanbezado TPA " + "ON CGC.C_FOLIO = TPA.cFolioSai " + "WHERE " + "CGCO.ID_TC = 29 AND " + "CGCO.ID_OPER IN (2,3) AND " + "EXISTS(SELECT 1 FROM tPAnticipadoDetalle A WHERE A.cMes <= DATEPART(MONTH,GETDATE()) AND A.nFolioPagoAnticipado = TPA.nFolioPagoAnticipado) " + "");
            rs = pstmntUp.executeQuery();
            todoBien = true;
            while (rs.next()) {
                AplicarContableReturn acr = null;
                ContableInterface conInt = new AplicacionContable();
                ArrayList<String> arrLResult = new ArrayList<String>();
                CasoBusinessLogic CBL = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
                Integer nFolioPoliza;
                Caso c = CBL.consultaCaso(rs.getInt("id_caso"), rs.getInt("id_caso_oper"));
                nFolioPoliza = rs.getInt("nFolioPoliza");
                Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                acr = conInt.cancelarAppContableNueva(conn, c, "tPAnticipadoEncanbezado", "tPAnticipadoDetalle", "nFolioPagoAnticipado", nFolioPoliza, "PAGOANTICIPADO", m, "/WEB-INF/mail-bodies/" + File.separator, usuario, "");
                final String[] resp = "CAPTURISTA_P_ANTICIPADO".split(";");
                final String[] oper = "captura_p_anticipado".split(";");
                CasoBusinessLogic casoTx = new CasoBusinessLogic(null);
                String prefixPath = "/WEB-INF/mail-bodies/" + File.separator;
                casoTx.avanzaCaso(c, usuario, "", resp, oper, m, prefixPath);
                arrLResult = (ArrayList) acr.getMessageList();
                if (acr.isSuccess()) {
                    todoBien = todoBien && true;
                } else {
                    mensaje += "El folio de poliza " + String.valueOf(nFolioPoliza) + " no se pudo cancelar. \n";
                    todoBien = todoBien && false;
                }
            }
            if (todoBien) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } catch (GestionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (pstmntUp != null)
                try {
                    pstmntUp.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            pstmntUp = null;
        }
        return mensaje;
    }

    public static int getFolio(String comentario) {
        int resp = -1;
        try {
            String[] splitComentario = comentario.split(" ");
            String[] split = splitComentario[0].split("-");
            resp = Integer.parseInt(split[2]);
        } catch (Exception e) {
            return resp;
        }
        return resp;
    }

    public static String getEstatus(int folio, Connection conn) throws SQLException {
        PreparedStatement pstmntUp = null;
        ResultSet rs = null;
        String retval = "";
        try {
            pstmntUp = conn.prepareStatement("SELECT CE.cEstatus FROM tPAnticipadoEncanbezado PA INNER JOIN tCatalogoEstatus CE	ON PA.cCveEstatus = CE.cCveEstatus WHERE nFolioPagoAnticipado = ?");
            pstmntUp.setInt(1, folio);
            rs = pstmntUp.executeQuery();
            if (rs.next()) {
                retval = rs.getString("cEstatus");
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmntUp != null)
                pstmntUp.close();
            pstmntUp = null;
        }
        return retval;
    }
}
