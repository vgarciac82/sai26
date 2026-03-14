package com.syc.contable.core;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.jfree.util.Log;
import com.syc.contable.RefasBusinessLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.DocPoliza.PolizasTrigger;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class RefasManager {

    public RefasManager() {
        super();
    }

    public synchronized static void actualizaUsuario(String nFolioRefas, String uLogin, String campoUsuario, String cCentroContable) {
        Connection conn = null;
        Statement statement = null;
        CasoBusinessLogic casoTx = new CasoBusinessLogic("jdbc/gestion");
        try {
            conn = casoTx.getConnection();
            statement = conn.createStatement();
            statement.executeUpdate("update CTRL_DOC..tRefasEncabezado set " + campoUsuario + "='" + uLogin + "' " + (!cCentroContable.equals("") ? " ,cCentroContable='" + cCentroContable + "'" : "") + " where nFolioRefas=" + nFolioRefas);
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
    }

    public static boolean insertaDetReintegroPaso(Connection conn, int folio, ReintegroDetalle datos, String evento, String cCentro, int consecutivo, String folioDep) throws SQLException {
        PreparedStatement pstmnt = null;
        PreparedStatement pstmntIns = null;
        ResultSet rs = null;
        boolean retval = false;
        try {
            String queryRFC = "SELECT ed.RFC FROM tEjercidoDetalle ed with(nolock), tEjercidoEncabezado ee with(NOLOCK) WHERE ee.caNoContrarrecibo = ? AND ee.nFolioEjercido = ed.nFolioEjercido and ed.nDocRenglon=?";
            String RFC = "";
            pstmnt = conn.prepareStatement(queryRFC);
            pstmnt.setString(1, datos.getCxp());
            pstmnt.setInt(2, datos.getnDocRenglon());
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                RFC = rs.getString(1);
            }
            pstmntIns = conn.prepareStatement("INSERT INTO tReintegroDetallePaso(noCLC,secCLC,EP,mImporteCLC,cMes,movto,cEvento,mImporte,mImporteNegativo,cCentroContable,nFolioReintegro,nDocRenglon,nCapitulo,cxp,RFC,nRenglonPagado,nFolioDependencia) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pstmntIns.setInt(1, datos.getNoCLC());
            pstmntIns.setInt(2, datos.getSecCLC());
            //pstmntIns.setInt(3, datos.getCodigoSaf());
            pstmntIns.setString(3, datos.getEP());
            //pstmntIns.setInt(5, datos.getCop());
            pstmntIns.setDouble(4, datos.getmImporteCLC());
            /*pstmntIns.setString(7, datos.getnCompromiso());
		    pstmntIns.setString(8, datos.getBeneficiario());
		    pstmntIns.setString(9, datos.getSuficiencia());
		    pstmntIns.setInt(10, datos.getSolOli());
		    pstmntIns.setString(11, datos.getTipoCon());
		    pstmntIns.setString(12, datos.getTipoDeCon());*/
            pstmntIns.setInt(5, datos.getMes());
            pstmntIns.setInt(6, datos.getMvto());
            //pstmntIns.setString(7, datos.getCxp());
            //pstmntIns.setString(8, datos.getClcSicop());
            /*pstmntIns.setDouble(15, datos.getIsr());
	   		pstmntIns.setDouble(16, datos.getIva());
		    pstmntIns.setDouble(17, datos.getMillar());
		    pstmntIns.setDouble(18, datos.getIvaDes());*/
            pstmntIns.setString(7, evento);
            pstmntIns.setDouble(8, datos.getmImporteCLC());
            pstmntIns.setDouble(9, datos.getmImporteCLC() * -1);
            pstmntIns.setString(10, cCentro);
            pstmntIns.setInt(11, folio);
            pstmntIns.setInt(12, consecutivo);
            pstmntIns.setString(13, datos.getnPartida());
            pstmntIns.setString(14, datos.getCxp());
            pstmntIns.setString(15, RFC);
            pstmntIns.setInt(16, datos.getnDocRenglon());
            pstmntIns.setString(17, folioDep);
            retval = pstmntIns.execute();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            try {
                if (pstmntIns != null) {
                    pstmntIns.close();
                }
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return retval;
    }

    public static boolean insertaDetReintegro(Connection conn, int folioReintegro, ReintegroDetalle datos, String evento, String cCentro, int nDocRenglon) throws SQLException {
        PreparedStatement pstmnt = null;
        PreparedStatement pstmntIns = null;
        boolean retval = false;
        //boolean existe = false;
        try {
            String query = "INSERT INTO tReintegroDetalle SELECT * FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, folioReintegro);
            //retval = pstmnt.execute();
            pstmnt.execute();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            try {
                if (pstmntIns != null) {
                    pstmntIns.close();
                }
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return retval;
    }

    public static boolean borraDetReintegro(Connection conn, int folioReintegro) throws SQLException {
        PreparedStatement pstmnt = null;
        boolean retval = false;
        try {
            String query = "DELETE FROM tReintegroDetalle WHERE nFolioReintegro = ?";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, folioReintegro);
            pstmnt.execute();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static boolean borraDetReintegroPaso(Connection conn, int folioReintegro) throws SQLException {
        PreparedStatement pstmnt = null;
        boolean retval = false;
        try {
            String query = "DELETE FROM tReintegroDetallePaso WHERE nFolioReintegro = ?";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, folioReintegro);
            pstmnt.execute();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static boolean insertaEncReintegro(Connection conn, int folioReintegro, ReintegroEncabezado datos, String cCentro, Date fecha) throws SQLException, ParseException {
        PreparedStatement pstmnt = null;
        PreparedStatement pstmntIns = null;
        PreparedStatement pstmntUp = null;
        ResultSet rs = null;
        boolean retval = false;
        boolean existe = false;
        try {
            String query = "SELECT * FROM tReintegroEncabezado with(NOLOCK) WHERE nFolioReintegro = ?";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, folioReintegro);
            //retval = pstmnt.execute();
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                existe = true;
            }
            if (existe) {
                //SI existe el registro le hacemos un update de datos, si no existe lo insertamos
                pstmntUp = conn.prepareStatement("UPDATE tReintegroEncabezado SET cTipoReintegro=?, aEjercicioFiscal=?, observaciones=?, concepto=?, formaDePago=?, clvRastreo=?, fichaDeposito=?, clvBanco=?, cuentaBancaria=?, lc=?, importeLC=? WHERE nFolioReintegro=?");
                pstmntUp.setString(1, datos.getcTipoReintegro());
                pstmntUp.setString(2, datos.getaEjercicioFiscal());
                pstmntUp.setString(3, datos.getObservaciones());
                pstmntUp.setString(4, datos.getConcepto());
                pstmntUp.setInt(5, datos.getFormaDePago());
                pstmntUp.setString(6, datos.getClvRastreo());
                pstmntUp.setString(7, datos.getFichaDeposito());
                pstmntUp.setString(8, datos.getClvBanco());
                pstmntUp.setString(9, datos.getCuentaBancaria());
                pstmntUp.setString(10, datos.getLc());
                pstmntUp.setString(11, datos.getImporteLC());
                pstmntUp.setDouble(12, datos.getnFolioReintegro());
                retval = pstmntUp.execute();
            } else if (datos != null) {
                pstmntIns = conn.prepareStatement("INSERT INTO tReintegroEncabezado(nFolioReintegro,fSolicitud,cTipoReintegro,cRamo,cUnidadResponsable,cDocumentoHaplicado,aEjercicioFiscal,observaciones,concepto,U_LOGIN,formaDePago,clvRastreo,fichaDeposito,clvBanco,cuentaBancaria,lc,importeLC,cTipoPoliza,fAplicacion,cUnidadResponsableContable,folioDependencia,mvto,aviso,tipoAviso,causaAviso,fAcredit) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                //folio del reintegro
                pstmntIns.setInt(1, datos.getnFolioReintegro());
                //fecha de hoy, pasar como parametro o recalcular
                pstmntIns.setDate(2, java.sql.Date.valueOf(datos.getfSolicitud()));
                //cTipoReintegro dependiendo si es de compromiso, directa
                pstmntIns.setString(3, datos.getcTipoReintegro());
                //cRamo
                pstmntIns.setString(4, datos.getcRamo());
                //cUnidadResponsable
                pstmntIns.setString(5, datos.getcUnidadResponsable());
                pstmntIns.setString(6, datos.getcDocumentoHaplicado());
                //aEjercicioFiscal (2012,2013...)
                pstmntIns.setString(7, datos.getaEjercicioFiscal());
                //observaciones
                pstmntIns.setString(8, datos.getObservaciones());
                //concepto
                pstmntIns.setString(9, datos.getConcepto());
                pstmntIns.setString(10, datos.getU_login());
                //forma de pago (efectivo, transferencia) [catalogo]
                pstmntIns.setInt(11, datos.getFormaDePago());
                //clave de rastreo
                pstmntIns.setString(12, datos.getClvRastreo());
                //ficha de deposito en el banco
                pstmntIns.setString(13, datos.getFichaDeposito());
                //clave del pago en el banco
                pstmntIns.setString(14, datos.getClvBanco());
                //cuenta bancaria
                pstmntIns.setString(15, datos.getCuentaBancaria());
                //linea de captura clave
                pstmntIns.setString(16, datos.getLc());
                //importe de la linea de captura
                pstmntIns.setDouble(17, Double.parseDouble(datos.getImporteLC()));
                //cTipoPoliza
                pstmntIns.setString(18, datos.getcTipoPoliza());
                pstmntIns.setDate(19, fecha);
                pstmntIns.setString(20, "B00");
                //pstmntIns.setString(21, datos.getCuentaPorPagar());
                pstmntIns.setInt(21, datos.getFolioDependencia());
                pstmntIns.setString(22, datos.getMovimiento());
                pstmntIns.setInt(23, datos.getAviso());
                pstmntIns.setInt(24, datos.getTipoAviso());
                pstmntIns.setInt(25, datos.getCausaAviso());
                pstmntIns.setString(26, datos.getfAcreditacion());
                retval = pstmntIns.execute();
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            try {
                if (pstmntUp != null) {
                    pstmntUp.close();
                }
                if (pstmntIns != null) {
                    pstmntIns.close();
                }
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return retval;
    }

    public static String getEvento(Connection conn, String cOBGINI, String cCTGA, String tipoCLC) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String evento = "";
        try {
            String query = "SELECT '" + tipoCLC + "' + cEVTO FROM tEventoConcepto with (nolock) WHERE cOBGINI = ? AND cCTGA = ?";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, cOBGINI);
            pstmnt.setString(2, cCTGA);
            pstmnt.execute();
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                evento = rs.getString(1);
            }
        } finally {
            try {
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return evento;
    }

    public static String getCLCPagada(Connection conn, int nCLC, String cxp) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        boolean retVal = false;
        String aplicado = "";
        String datos = "";
        try {
            String query = "SELECT ISNULL(cDocumentoHaplicado, CASE (SELECT COUNT( * ) FROM TADEFAENCABEZADO a with(nolock) WHERE a.caNoContrarrecibo = p.caNoContrarrecibo ) WHEN 1 THEN 'S' ELSE NULL END)" + " FROM tPagadoEncabezado p with (nolock) WHERE p.caNoContrarrecibo = ?";
            pstmnt = conn.prepareStatement(query);
            //pstmnt.setInt(1, nCLC);
            pstmnt.setString(1, cxp);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                aplicado = rs.getString(1);
                if ("S".equals(aplicado)) {
                    retVal = true;
                } else if (aplicado == null) {
                    retVal = false;
                    datos = "La CLC con Cuenta Por Pagar: " + cxp + " NO se encuentra pagada o como Adefa";
                }
            } else {
                retVal = false;
                datos = "La CLC con Cuenta Por Pagar: " + cxp + " NO existe";
            }
        } finally {
            try {
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return datos;
    }

    public static String tipoPago(Connection conn, int nCLC, String cxp) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String tipoCLC = "";
        try {
            //String query="SELECT ctipoPago FROM tPagadoEncabezado with (nolock) WHERE caNoContrarrecibo = ?"; //SE CAMBIA EL QUERY PORQUE PIDEN QUE SE UTILICE LA INFORMACION DEL SIAFF
            String query = " SELECT TIPO_CLC FROM tPagadoEncabezado, CLC_SIAFF_ENC WHERE caNoContrarrecibo = ? AND nFolioSIAFF = FOLIO_CLC";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, cxp);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                tipoCLC = rs.getString(1);
            }
        } finally {
            try {
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return tipoCLC;
    }

    public static String getTipoPago(Connection conn, String cxp) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String tipo = "";
        try {
            String query = "SELECT CASE cTipoPago WHEN 'PAGOOBRA' THEN 'COMPROMISO' WHEN 'PAGODIVERSO' THEN 'COMPROMISO' WHEN 'PAGODIRECTO' THEN 'DIRECTA' WHEN 'RELACIONGASTOS' THEN 'DIRECTA' WHEN 'NOMINA' THEN 'COMPROMISO' when 'AJENAS' then 'DIRECTA' when 'FEDERALIZADO' then 'COMPROMISO' " + "ELSE '' END AS dTipoPago FROM tPagadoEncabezado with (nolock) WHERE caNoContrarrecibo = ?";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, cxp);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                tipo = rs.getString(1);
            }
        } finally {
            try {
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return tipo;
    }

    public static ReintegroDetalle[] getReintegrosDetalle(Connection conn, String[] ids, int folio) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        ReintegroDetalle[] reintegros;
        try {
            String query = "SELECT * FROM tReintegroDetalle rd with (nolock) WHERE nFolioReintegro = ? AND (";
            for (int j = 0; j < ids.length; j++) {
                query += " nDocRenglon=? OR";
            }
            query = query.substring(0, query.length() - 3);
            query += ")";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, folio);
            for (int j = 0; j < ids.length; j++) {
                pstmnt.setInt(j + 2, Integer.parseInt(ids[j]));
            }
            rs = pstmnt.executeQuery();
            //reintegros = new ReintegroDetalle[rs.getFetchSize()];
            reintegros = new ReintegroDetalle[ids.length];
            int i = 0;
            while (rs.next()) {
                ReintegroDetalle rd = new ReintegroDetalle();
                rd.setNoCLC(rs.getInt("noCLC"));
                rd.setSecCLC(rs.getInt("secCLC"));
                //rd.setCodigoSaf(rs.getInt("codigoSaf"));
                rd.setEP(rs.getString("EP"));
                //rd.setCop(rs.getInt("cop"));
                rd.setmImporteCLC(rs.getDouble("mImporteCLC"));
                DecimalFormat formatter = new DecimalFormat("###.####");
                rd.setmImporteCLCFormat(formatter.format(rs.getDouble("mImporteCLC")));
                /*rd.setnCompromiso(rs.getString("nCompromiso"));
			rd.setBeneficiario(rs.getString("beneficiario"));
			rd.setSuficiencia(rs.getString("suficiencia"));
			rd.setSolOli(rs.getInt("solOli"));
			rd.setTipoCon(rs.getString("tipoCon"));
			rd.setTipoDeCon(rs.getString("tipoDeCon"));*/
                rd.setMes(rs.getInt("cMes"));
                rd.setCxp(rs.getString("cxp"));
                /*rd.setMvto(rs.getInt("movto"));
			rd.setIsr(rs.getInt("isr"));
			rd.setIva(rs.getInt("iva"));
			rd.setMillar(rs.getInt("millar"));
			rd.setIvaDes(rs.getInt("ivaDes"));*/
                reintegros[i] = rd;
                i++;
            }
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return reintegros;
    }

    public static void autorizaReintegro(Connection conn, Caso c, String nNumSicop, String cRecMotivSicop, String nNumMAP, String cRecMotivMAP, String ulogin, String prefixPath) throws SQLException, GestionException {
        PreparedStatement pstmnt = null;
        int nIdCaso;
        String cSQLString = "";
        ResultSet rs = null;
        int nExiste = 0;
        try {
            nIdCaso = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            /*cSQLString = "select COUNT(*) from tReintegroAutEncabezado with (nolock) WHERE nFolioReintegroaut = " + nIdCaso;
			pstmnt = conn.prepareStatement(cSQLString);
			rs = pstmnt.executeQuery();
			if (rs.next()) {
				nExiste = rs.getInt(1);
			}
			if (nExiste == 0) {*/
            pstmnt = conn.prepareStatement("INSERT INTO tReintegroAutEncabezado (nFolioReintegroaut, fSolicitud, cTipoReintegro, cRamo, cUnidadResponsable, cDocumentoHaplicado, aEjercicioFiscal, observaciones, concepto, u_login, " + "cUnidadResponsableContable,nFolioTramiteSicop, cTipoPoliza, fAplicacion,cdescripcionpoliza, formaDePago, clvRastreo,fichaDeposito, clvBanco,cuentaBancaria,lc,importeLC,clcSicop,folioDependencia,mvto,aviso,tipoAviso,causaAviso) " + "SELECT nFolioReintegro, fSolicitud, cTipoReintegro, cRamo,cUnidadResponsable, 'N', aEjercicioFiscal, observaciones, concepto, " + "u_login, 'B00',nFolioTramiteSicop, 'PD', fAplicacion,'AUTORIZACION DE REINTEGRO PRESUPUESTAL FOLIO ' + CAST(nFolioReintegro as varchar),formaDePago, clvRastreo,fichaDeposito, clvBanco,cuentaBancaria,lc,importeLC,clcSicop,folioDependencia,mvto,aviso,tipoAviso,causaAviso FROM tReintegroEncabezado WITH (NOLOCK) WHERE nFolioReintegro = ?");
            //pstmnt.setString(1, nNumSicop);
            /*pstmnt.setString(2, cRecMotivSicop);
				pstmnt.setString(3, nNumMAP);
				pstmnt.setString(4, cRecMotivMAP);*/
            pstmnt.setInt(1, nIdCaso);
            pstmnt.execute();
            /*pstmnt = conn.prepareStatement("INSERT INTO tReintegroAutDetalle(nFolioReintegroaut,nDocRenglon,noCLC,secCLC,EP,mImporteCLC,cMes,movto,cuentaPorPagar,clcSicop,cEvento, mImporte, mImporteNegativo, cCentroContable, nCapitulo) "
				        + "SELECT nFolioReintegro,nDocRenglon, noCLC,secCLC,EP,mImporteCLC,cMes,movto,cuentaPorPagar,clcSicop, SUBSTRING(cEvento,1,3) + 'TA_' + SUBSTRING(cEvento,4,LEN(cEvento)), mImporte, mImporteNegativo, cCentroContable, nCapitulo "
				        + "FROM tReintegroDetalle WITH (NOLOCK) WHERE nFolioReintegro = ?");*/
            pstmnt = conn.prepareStatement("SELECT tipoAviso FROM tReintegroEncabezado WITH (NOLOCK) WHERE nFolioReintegro = ?");
            pstmnt.setInt(1, nIdCaso);
            rs = pstmnt.executeQuery();
            String tipoRein = "";
            if (rs.next()) {
                tipoRein = rs.getString(1);
            }
            //}
            /*if("2".equals(tipoRein)){
				    tipoRein = "RC";
				}else{
					tipoRein = "RD"; //Rechazo SPEI siempre es directo segun lo platicado 9 noviembre
				}*/
            pstmnt = conn.prepareStatement("INSERT INTO tReintegroAutDetalle(nFolioReintegroaut,nDocRenglon,noCLC,secCLC,EP,mImporteCLC,cMes,movto,cEvento, mImporte, mImporteNegativo, cCentroContable, nCapitulo,cxp,RFC,nFolioDependencia,ALM,nRenglonPagado) " + "SELECT rd.nFolioReintegro,rd.nDocRenglon, rd.noCLC,rd.secCLC,rd.EP,rd.mImporteCLC,rd.cMes,rd.movto,(SELECT CASE cTipoPago WHEN 'PAGOOBRA' THEN 'RC' WHEN 'PAGODIVERSO' THEN 'RC' WHEN 'PAGODIRECTO' THEN 'RD' WHEN 'RELACIONGASTOS' THEN 'RD' WHEN 'NOMINA' THEN 'RC' when 'AJENAS' then (SELECT CASE cTipoDoc WHEN 'PAGODIRECTO' THEN 'RD' ELSE 'RC' END FROM tOperAjenasDetalle WHERE rd.cxp=caNoContrarrecibo) when 'FEDERALIZADO' then 'RC' " + " ELSE '' END AS dTipoPago FROM tPagadoEncabezado WITH(NOLOCK) WHERE caNoContrarrecibo = rd.cxp) + SUBSTRING((SELECT distinct cEvento FROM tEjercidoDetalle ed WHERE ed.EP=rd.EP AND  ed.nFolioPAGO=(SELECT nFolioPAGO FROM tPagadoEncabezado WITH(NOLOCK) WHERE caNoContrarrecibo=rd.cxp) AND ed.cTipoPago=(SELECT cTipoPago FROM tPagadoEncabezado WITH(NOLOCK) WHERE caNoContrarrecibo=rd.cxp)),2,LEN((SELECT distinct cEvento FROM tEjercidoDetalle ed WHERE ed.EP=rd.EP AND ed.nFolioPAGO=(SELECT nFolioPAGO FROM tPagadoEncabezado WITH(NOLOCK) WHERE caNoContrarrecibo=rd.cxp) AND ed.cTipoPago=(SELECT cTipoPago FROM tPagadoEncabezado WITH(NOLOCK) WHERE caNoContrarrecibo=rd.cxp)))), rd.mImporte, rd.mImporteNegativo, rd.cCentroContable, rd.nCapitulo, rd.cxp, rd.RFC, rd.nFolioDependencia, " + " (select top(1) ALM from tPagadoDetalle with(nolock), tPagadoEncabezado with(nolock) where EP=rd.EP and nFolioSIAFF=rd.noCLC" + " and tPagadoDetalle.nFolioPagado=tPagadoEncabezado.nFolioPagado and tPagadoEncabezado.caNoContrarrecibo=rd.cxp) as ALM, rd.nRenglonPagado " + " FROM tReintegroDetalle rd WITH (NOLOCK) WHERE rd.nFolioReintegro = ?");
            pstmnt.setInt(1, nIdCaso);
            pstmnt.execute();
            //CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            // avanza casos
            //cbl.avanzaCaso(c, ulogin, "", new String[] { "CONSULTA_REINTEGRO" }, new String[] { "consulta_reintegro" }, new HashMap(), prefixPath);
            conn.commit();
        } catch (SQLException s) {
            conn.rollback();
            s.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            if (rs != null)
                rs.close();
            pstmnt = null;
            rs = null;
        }
    }

    public static String getTipoPoliza(Connection conn, String cDocumento) throws SQLException {
        String retTipoPoliza = "";
        ResultSet rs = null;
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("select cTipoPoliza from tDocumentoTipoPoliza with (nolock) where cDocumento=?");
            pstm.setString(1, cDocumento);
            rs = pstm.executeQuery();
            if (rs.next()) {
                retTipoPoliza = rs.getString(1);
            }
        } catch (SQLException e) {
            Log.info("Error occurred", e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return retTipoPoliza;
    }

    public static String getcPartida(Connection conn, String EP, String ejercicio) throws SQLException {
        String cPartida = "";
        ResultSet rs = null;
        PreparedStatement pstm = null;
        String res = "";
        try {
            pstm = conn.prepareStatement("select CLAVE_SIAFF from CTRL_DOC..t_Refas" + ejercicio + " with (nolock) where CLAVE_SIAFF=?");
            pstm.setString(1, EP);
            rs = pstm.executeQuery();
            if (rs.next()) {
                cPartida = rs.getString(1);
            }
        } catch (SQLException e) {
            Log.info("Error occurred", e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        if (cPartida != null && !cPartida.equals("")) {
            res = getPartida(cPartida);
        } else {
            res = "La EP " + EP + " no existe en el catalogo";
        }
        return res;
    }

    private static String getPartida(String cPartida) {
        String[] split = cPartida.split("\\.");
        String res = split[9].trim();
        res = res.substring(0, 1);
        for (int i = 1; i <= 4; i++) res += "0";
        return res;
    }

    public static String getEntidadFederativa(Connection conn, String EP) throws SQLException {
        String cEntidadFederativa = "";
        ResultSet rs = null;
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("select cEntidadFederativa from tCatalogoEP with (nolock) where EP=?");
            pstm.setString(1, EP);
            rs = pstm.executeQuery();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return cEntidadFederativa;
    }

    public static double getRemanente(Connection conn, String ep, String cxp, int nRenglon) throws SQLException {
        double remanente = 0.00;
        ResultSet rs = null;
        PreparedStatement pstm = null;
        try {
            String queryRemanente = "SELECT CASE tReintegros+tRectificacion " + " WHEN 0 THEN mImporteNeto " + " ELSE " + " CASE WHEN tReintegros!=0 AND tRectificacion=0 THEN mImporteNeto-impReint ELSE" + " impRect-impReint" + " END " + " END as Remanente" + " FROM (SELECT pd.mImporteNeto," + " (select COUNT(*) as sumaRect FROM tRectificacionEncabezado re with (nolock), tRectificacionDetalle rd with (nolock) WHERE rd.EP=? AND re.cDocumentoHaplicado in ('S','N') AND re.nFolioRectificacion=rd.nFolioRectificacion AND re.caNoContrarrecibo = ? AND rd.EP=pd.EP and rd.nDocRenglon=?)as tRectificacion," + " (select COUNT(*) as sumaReint FROM tReintegroEncabezado ree with (nolock), tReintegroDetalle red with (nolock) WHERE red.EP=? AND ree.cDocumentoHaplicado in ('S','N') AND ree.nFolioReintegro=red.nFolioReintegro AND red.cxp= ? AND red.EP=pd.EP and red.nRenglonPagado=?) as tReintegros," + " ISNULL((select ISNULL(SUM(rd.mImporte),0) as impR from tReintegroEncabezado re WITH(NOLOCK),tReintegroDetalle rd WITH(NOLOCK) where re.nFolioReintegro =rd.nFolioReintegro and re.cDocumentoHaplicado in ('S','N') and rd.cxp=? and rd.EP=? and rd.nRenglonPagado=?),0) as impReint," + " ISNULL((select ISNULL(SUM(case substring(cEvento,1,4)  when 'DICE' then  mImporte " + " WHEN 'DEBE' then mImporteNegativo end ),0) as imp " + " FROM tRectificacionDetalle r with (nolock), tRectificacionEncabezado h with (nolock)" + " WHERE r.nFolioRectificacion = h.nFolioRectificacion " + " AND h.caNoContrarrecibo = ? and r.nDocRenglon  = ? AND h.cDocumentoHaplicado in ('S','N') " + " AND ep not in (select  ep from tRectificacionDetalle d with (nolock) WHERE d.nDocRenglon = r.nDocRenglon" + " AND d.cEvento like 'DICE%' and d.ep= ?)" + " GROUP BY r.nDocRenglon),0) as impRect" + " FROM tPagadoEncabezado pe with (nolock), tPagadoDetalle pd with (nolock)" + " WHERE caNoContrarrecibo=? AND pd.EP=? AND " + " ( pe.cDocumentoHaplicado='S' or exists(select * from tAdefaEncabezado where caNoContrarrecibo = ? and cDocumentoHaplicado='S')) AND " + " pe.nFolioPagado=pd.nFolioPagado AND pd.nDocRenglon=?) tNueva" + " WHERE 1=1";
            pstm = conn.prepareStatement(queryRemanente);
            pstm.setString(1, ep);
            pstm.setString(2, cxp);
            pstm.setInt(3, nRenglon);
            pstm.setString(4, ep);
            pstm.setString(5, cxp);
            pstm.setInt(6, nRenglon);
            pstm.setString(7, cxp);
            pstm.setString(8, ep);
            pstm.setInt(9, nRenglon);
            pstm.setString(10, cxp);
            pstm.setInt(11, nRenglon);
            pstm.setString(12, ep);
            pstm.setString(13, cxp);
            pstm.setString(14, ep);
            pstm.setString(15, cxp);
            pstm.setInt(16, nRenglon);
            rs = pstm.executeQuery();
            if (rs.next()) {
                remanente = rs.getDouble(1);
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
        return remanente;
    }

    public static ReintegroEncabezado getReintegroEncabezado(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        ReintegroEncabezado re = new ReintegroEncabezado();
        try {
            pstm = conn.prepareStatement("SELECT * FROM tReintegroEncabezado with(nolock) WHERE nFolioReintegro = ?");
            pstm.setInt(1, folio);
            res = pstm.executeQuery();
            if (res.next()) {
                re = new ReintegroEncabezado();
                re.setfSolicitud(res.getString("fSolicitud"));
                re.setcTipoReintegro(res.getString("cTipoReintegro"));
                re.setfAplicacion(res.getString("fAplicacion"));
                re.setcRamo(res.getString("cRamo"));
                re.setcUnidadResponsable(res.getString("cUnidadResponsable"));
                re.setcUnidadResponsableContable(res.getString("cUnidadResponsableContable"));
                re.setcDocumentoHaplicado(res.getString("cDocumentoHaplicado"));
                re.setnFolioPoliza(res.getInt("nFolioPoliza"));
                re.setcTipoPoliza(res.getString("cTipoPoliza"));
                re.setaEjercicioFiscal(res.getString("aEjercicioFiscal"));
                re.setObservaciones(res.getString("observaciones"));
                re.setConcepto(res.getString("concepto"));
                re.setU_login(res.getString("U_LOGIN"));
                re.setfCancelacion(res.getString("fCancelacion"));
                re.setfExpiracion(res.getString("fExpiracion"));
                re.setfAcreditacion(res.getString("fAcredit"));
                re.setFormaDePago(res.getInt("formaDePago"));
                re.setClvRastreo(res.getString("clvRastreo"));
                re.setFichaDeposito(res.getString("fichaDeposito"));
                re.setClvBanco(res.getString("clvBanco"));
                re.setCuentaBancaria(res.getString("cuentaBancaria"));
                re.setLc(res.getString("lc"));
                DecimalFormat formatter = new DecimalFormat("###,###.##");
                re.setImporteLC(formatter.format(res.getDouble("importeLC")));
                re.setCausaAviso(res.getInt("causaAviso"));
                re.setTipoAviso(res.getInt("tipoAviso"));
                re.setMovimiento(res.getString("mvto"));
                //re.setCuentaPorPagar(res.getString("cuentaPorPagar"));
                //re.setFolioDependencia(Integer.parseInt(res.getString("folioDependencia"))); //este ya lo tiene en el detalle, por lo que no se toma en cuenta
            }
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (res != null) {
                    res.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return re;
    }

    public static ReintegroEncabezadoMil getReintegroEncabezadoNuevo(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        ReintegroEncabezadoMil re = null;
        try {
            pstm = conn.prepareStatement("SELECT * FROM CTRL_DOC..tRefasEncabezado with(nolock) WHERE nFolioRefas = ?");
            pstm.setInt(1, folio);
            res = pstm.executeQuery();
            if (res.next()) {
                re = new ReintegroEncabezadoMil();
                re.setfSolicitud(res.getString("fSolicitud"));
                re.setcTipoReintegro(res.getString("cTipoRefas"));
                re.setfAplicacion(res.getString("fAplicacion"));
                re.setcRamo(res.getString("cRamo"));
                re.setcUnidadResponsable(res.getString("cUnidadResponsable"));
                re.setcUnidadResponsableContable(res.getString("cUnidadResponsableContable"));
                re.setcDocumentoHaplicado(res.getString("cDocumentoHaplicado"));
                re.setnFolioPoliza(res.getInt("nFolioPoliza"));
                re.setcTipoPoliza(res.getString("cTipoPoliza"));
                re.setaEjercicioFiscal(res.getString("aEjercicioFiscal"));
                re.setObservaciones(res.getString("observaciones"));
                re.setConcepto(res.getString("concepto"));
                re.setfCancelacion(res.getString("fCancelacion"));
                re.setfExpiracion(res.getString("fExpiracion"));
                re.setfAcreditacion(res.getString("fAcredit"));
                re.setFormaDePago(res.getString("formaDePago"));
                re.setClvRastreo(res.getString("clvRastreo"));
                re.setFichaDeposito(res.getString("fichaDeposito"));
                re.setClvBanco(res.getString("clvBanco"));
                re.setCuentaBancaria(res.getString("cuentaBancaria"));
                re.setLc(res.getString("lc"));
                DecimalFormat formatter = new DecimalFormat("###,###.##");
                re.setImporteLC(formatter.format(res.getDouble("importeLC")));
                re.setCausaAviso(res.getString("causaAviso"));
                re.setTipoAviso(res.getString("tipoAviso"));
                re.setMovimiento(res.getString("mvto"));
                re.setFolioDependencia(res.getString("folioDependencia"));
                re.setnFolioTramiteSiaff(res.getString("nFolioTramiteSiaff"));
                re.setnFolioTramiteSicop(res.getString("nFolioTramiteSicop"));
            }
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (res != null) {
                    res.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return re;
    }

    public static ArrayList<ReintegroDetalle> getRefasDetalle(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        ReintegroDetalle rd = null;
        ArrayList<ReintegroDetalle> detalles = new ArrayList<ReintegroDetalle>();
        try {
            pstm = conn.prepareStatement("SELECT * FROM CTRL_DOC..tRefasDetalle with(nolock) WHERE nFolioRefas = ?");
            pstm.setInt(1, folio);
            res = pstm.executeQuery();
            while (res.next()) {
                rd = new ReintegroDetalle();
                rd.setNoCLC(res.getInt("noCLC"));
                rd.setnDocRenglon(res.getInt("nDocRenglon"));
                rd.setSecCLC(res.getInt("secCLC"));
                rd.setEP(res.getString("EP"));
                rd.setmImporteCLC(res.getDouble("mImporteCLC"));
                DecimalFormat formatter = new DecimalFormat("###.####");
                rd.setmImporteCLCFormat(formatter.format(res.getDouble("mImporteCLC")));
                rd.setMes(res.getInt("cMes"));
                rd.setCxp(res.getString("cxp"));
                detalles.add(rd);
            }
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (res != null) {
                    res.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return detalles;
    }

    public static boolean getReintegroHApartado(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        boolean aplicado = false;
        try {
            pstm = conn.prepareStatement("SELECT cDocumentoHaplicado FROM tReintegroEncabezado with(nolock) WHERE nFolioReintegro = ?");
            pstm.setInt(1, folio);
            res = pstm.executeQuery();
            if (res.next()) {
                if ("S".equals(res.getString("cDocumentoHaplicado")))
                    aplicado = true;
            }
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (res != null) {
                    res.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return aplicado;
    }

    public static String getMesesImportes(Connection conn, String EP, String CXP) throws SQLException {
        String res = "";
        ResultSet rs = null;
        PreparedStatement pstm = null;
        try {
            //AND pe.cDocumentoHaplicado='S'
            pstm = conn.prepareStatement("SELECT pd.nDocRenglon,pd.cMes, pd.mImporteNeto FROM tPagadoEncabezado pe with(nolock), tPagadoDetalle pd with(nolock) WHERE pd.nFolioPagado=pe.nFolioPagado AND pd.EP=? AND pe.caNoContrarrecibo=? ");
            pstm.setString(1, EP);
            pstm.setString(2, CXP);
            rs = pstm.executeQuery();
            int contador = 0;
            res = "--Para la EP " + EP + " en la CXP " + CXP + ", hay:\\n";
            while (rs.next()) {
                res += "en el Renglon " + rs.getInt("nDocRenglon") + " Mes " + rs.getInt("cMes") + " el Importe de " + rs.getDouble("mImporteNeto") + "\\n";
                contador++;
            }
            if (contador == 0) {
                res = "La cuenta por pagar proporcionada no se encuentra pagada\\n";
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
        return res;
    }

    public static boolean getEPCatalogo(Connection conn, String EP, String ejercicio, String clc) throws SQLException {
        boolean res = false;
        ResultSet rs = null;
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("SELECT COUNT(*) FROM CTRL_DOC..t_Refas" + ejercicio + " with(nolock) WHERE CLAVE_SIAFF=? AND CLC =?");
            pstm.setString(1, EP.trim());
            pstm.setString(2, clc.trim());
            rs = pstm.executeQuery();
            if (rs.next()) {
                int cuantos = rs.getInt(1);
                if (cuantos > 0)
                    res = true;
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
        return res;
    }

    public static boolean actualizaInfoPagos(Connection conn, int folio, String clvRastreo, String lc, String ficha, String clvBanco, String cuenta, String fAcredit) throws SQLException, ParseException {
        PreparedStatement pstmntUp = null;
        boolean retval = false;
        try {
            pstmntUp = conn.prepareStatement("UPDATE CTRL_DOC..tRefasEncabezado SET clvRastreo=?, lc=?, fichaDeposito=?, clvBanco=?, cuentaBancaria=?, fAcredit=? WHERE nFolioRefas=?");
            pstmntUp.setString(1, clvRastreo);
            pstmntUp.setString(2, lc);
            pstmntUp.setString(3, ficha);
            pstmntUp.setString(4, clvBanco);
            pstmntUp.setString(5, cuenta);
            pstmntUp.setString(6, fAcredit);
            pstmntUp.setInt(7, folio);
            retval = pstmntUp.execute();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmntUp != null)
                pstmntUp.close();
            pstmntUp = null;
        }
        return retval;
    }

    public static boolean catalogoMovimiento(Connection conn, String movimiento) throws SQLException, ParseException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        boolean retval = false;
        try {
            pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoMvto with(nolock) WHERE tipo = ?");
            pstm.setString(1, movimiento);
            rs = pstm.executeQuery();
            if (rs.next()) {
                int cuantos = rs.getInt(1);
                if (cuantos > 0)
                    retval = true;
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
        return retval;
    }

    public static boolean catalogoCausaAviso(Connection conn, int causa, int tipo) throws SQLException, ParseException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        boolean retval = false;
        try {
            pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoCausaAviso with(nolock) WHERE aCausa = ?");
            pstm.setInt(1, causa);
            rs = pstm.executeQuery();
            if (rs.next()) {
                int cuantos = rs.getInt(1);
                if (cuantos > 0)
                    retval = true;
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
        return retval;
    }

    public static int secCLC(Connection conn, String caNoContrarrecibo, String EP, int folio) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int retval = 0;
        try {
            pstmnt = conn.prepareStatement("SELECT SEC FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=? and SEC not in (SELECT secCLC FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=? and noCLC=? and EP=?)");
            pstmnt.setString(1, caNoContrarrecibo);
            pstmnt.setString(2, EP);
            pstmnt.setInt(3, folio);
            pstmnt.setString(4, caNoContrarrecibo);
            pstmnt.setString(5, EP);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retval = rs.getInt(1);
            } else {
                retval = -1;
            }
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmnt != null) {
                    pstmnt.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return retval;
    }

    public static int secCLCRect(Connection conn, String caNoContrarrecibo, String EP) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int retval = 0;
        try {
            pstmnt = conn.prepareStatement("SELECT SEC FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=?");
            pstmnt.setString(1, caNoContrarrecibo);
            pstmnt.setString(2, EP);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retval = rs.getInt(1);
            } else {
                retval = -1;
            }
        } catch (SQLException s) {
            Log.info("Object: {}", s);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmnt != null) {
                    pstmnt.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return retval;
    }

    public static String tipoConcepto(Connection conn, String caNoContrarrecibo, String EP) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String retval = "";
        try {
            pstmnt = conn.prepareStatement("SELECT TCONC_49 FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=?");
            pstmnt.setString(1, caNoContrarrecibo);
            pstmnt.setString(2, EP);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retval = rs.getString(1);
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmnt != null) {
                    pstmnt.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return retval;
    }

    public static String folioDependencia(Connection conn, String caNoContrarrecibo, String EP, int folio) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String retval = "";
        try {
            pstmnt = conn.prepareStatement("SELECT NCLC_43 FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=? ");
            pstmnt.setString(1, caNoContrarrecibo);
            pstmnt.setString(2, EP);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retval = rs.getString(1);
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            try {
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return retval;
    }

    public static String getRFC(Connection conn, String caNoContrarrecibo, String ep) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String retval = "";
        try {
            String queryRFC = "SELECT top(1) ed.RFC FROM tEjercidoDetalle ed with(nolock), tEjercidoEncabezado ee with(NOLOCK) WHERE ee.caNoContrarrecibo = ? AND ee.nFolioEjercido = ed.nFolioEjercido and ed.EP=?";
            pstmnt = conn.prepareStatement(queryRFC);
            pstmnt.setString(1, caNoContrarrecibo);
            pstmnt.setString(2, ep);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retval = rs.getString(1);
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            try {
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return retval;
    }

    public static int[] getNDocRenglon(Connection conn, String caNoContrarrecibo, String EP, int cMes, int folio) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int[] retval = new int[100];
        try {
            int i = 0;
            //pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon not in(SELECT nRenglonPagado FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?) order by pd.mImporteNeto");
            pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=?  order by pd.mImporteNeto");
            pstmnt.setString(1, caNoContrarrecibo);
            pstmnt.setString(2, EP);
            pstmnt.setInt(3, cMes);
            //pstmnt.setInt(4, folio);
            rs = pstmnt.executeQuery();
            retval[0] = -1;
            while (rs.next()) {
                retval[i] = rs.getInt(1);
                i++;
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            try {
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return retval;
    }

    public static int getDetallePasoTotal(Connection conn, int folio) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int retval = 0;
        try {
            pstmnt = conn.prepareStatement("SELECT count(*) FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?");
            pstmnt.setInt(1, folio);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retval = rs.getInt(1);
            } else {
                retval = -1;
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            try {
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return retval;
    }

    public static boolean borraReintegro(Connection conn, int folioReintegro) throws SQLException {
        PreparedStatement pstmnt = null;
        boolean retval = false;
        try {
            String queryEnc = "DELETE FROM tReintegroEncabezado WHERE nFolioReintegro = ?";
            String queryDet = "DELETE FROM tReintegroDetalle WHERE nFolioReintegro = ?";
            pstmnt = conn.prepareStatement(queryDet);
            pstmnt.setInt(1, folioReintegro);
            pstmnt.execute();
            pstmnt = conn.prepareStatement(queryEnc);
            pstmnt.setInt(1, folioReintegro);
            pstmnt.execute();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static void insertaRefas(Connection conn, ReintegroEncabezadoMil reinE, ArrayList<ReintegroDetalleMil> reinDetalles, int folio, String folioCompleto, Usuario usuario, String ejercicioRefa) throws Exception {
        PreparedStatement psInsertDetalle = null;
        PreparedStatement psInsertEncabezado = null;
        try {
            psInsertEncabezado = conn.prepareStatement("INSERT INTO CTRL_DOC.dbo.tRefasEncabezado(nFolioRefas,fSolicitud,cTipoRefas,cRamo,cUnidadResponsable,cDocumentoHaplicado,aEjercicioFiscal,observaciones,concepto,U_LOGIN,formaDePago,clvRastreo,fichaDeposito,clvBanco,cuentaBancaria,lc,importeLC,cTipoPoliza,fAplicacion,cUnidadResponsableContable,folioDependencia,mvto,aviso,tipoAviso,causaAviso,fAcredit,ejercicioRefas,uCaptura) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            //folio del Refas
            psInsertEncabezado.setInt(1, folio);
            //fecha de hoy, pasar como parametro o recalcular
            psInsertEncabezado.setString(2, reinE.getfSolicitud());
            //cTipoReintegro dependiendo si es de compromiso, directa
            psInsertEncabezado.setString(3, reinE.getcTipoReintegro());
            //cRamo
            psInsertEncabezado.setString(4, reinE.getcRamo());
            //cUnidadResponsable
            psInsertEncabezado.setString(5, reinE.getcUnidadResponsable());
            psInsertEncabezado.setString(6, reinE.getcDocumentoHaplicado());
            //aEjercicioFiscal (2012,2013...)
            psInsertEncabezado.setString(7, reinE.getaEjercicioFiscal());
            //observaciones
            psInsertEncabezado.setString(8, reinE.getObservaciones());
            //concepto
            psInsertEncabezado.setString(9, reinE.getConcepto());
            psInsertEncabezado.setString(10, usuario.getLogin());
            //forma de pago (efectivo, transferencia) [catalogo]
            psInsertEncabezado.setString(11, reinE.getFormaDePago());
            //clave de rastreo
            psInsertEncabezado.setString(12, reinE.getClvRastreo());
            //ficha de deposito en el banco
            psInsertEncabezado.setString(13, reinE.getFichaDeposito());
            //clave del pago en el banco
            psInsertEncabezado.setString(14, reinE.getClvBanco());
            //cuenta bancaria
            psInsertEncabezado.setString(15, reinE.getCuentaBancaria());
            //linea de captura clave
            psInsertEncabezado.setString(16, reinE.getLc());
            //importe de la linea de captura
            psInsertEncabezado.setDouble(17, Double.parseDouble(reinE.getImporteLC()));
            //cTipoPoliza
            psInsertEncabezado.setString(18, reinE.getcTipoPoliza());
            psInsertEncabezado.setString(19, reinE.getfAplicacion());
            psInsertEncabezado.setString(20, reinE.getcUnidadResponsableContable());
            psInsertEncabezado.setString(21, reinE.getFolioDependencia());
            psInsertEncabezado.setString(22, reinE.getMovimiento());
            psInsertEncabezado.setString(23, reinE.getAviso());
            psInsertEncabezado.setString(24, reinE.getTipoAviso());
            psInsertEncabezado.setString(25, reinE.getCausaAviso());
            psInsertEncabezado.setString(26, reinE.getfAcreditacion());
            psInsertEncabezado.setString(27, ejercicioRefa);
            psInsertEncabezado.setString(28, usuario.getLogin());
            psInsertEncabezado.execute();
            //String query  = "INSERT INTO CTRL_DOC.dbo.tRefasDetalle(nFolioRefas,nDocRenglon,noCLC,secCLC,cEvento,EP,mImporteCLC,mImporte,mImporteNegativo,cMes,cCentroContable,nCapitulo,cxp,RFC,nRenglonPagado,nFolioDependencia,ALM)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            String buf = "INSERT INTO CTRL_DOC.dbo.tRefasDetalle(nFolioRefas,nDocRenglon,noCLC,secCLC,cEvento,EP,mImporteCLC,mImporte,mImporteNegativo,cMes,cCentroContable,nCapitulo,cxp,RFC,nRenglonPagado,nFolioDependencia,ALM)VALUES(";
            psInsertDetalle = conn.prepareStatement("INSERT INTO CTRL_DOC.dbo.tRefasDetalle(nFolioRefas,nDocRenglon,noCLC,secCLC,cEvento,EP,mImporteCLC,mImporte,mImporteNegativo,cMes,cCentroContable,nCapitulo,cxp,RFC,nRenglonPagado,nFolioDependencia,ALM)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            for (Iterator<ReintegroDetalleMil> i = reinDetalles.iterator(); i.hasNext(); ) {
                ReintegroDetalleMil rd = i.next();
                String cCentroContable = "-50";
                psInsertDetalle.setInt(1, reinE.getnFolioReintegro());
                buf = buf + "'" + reinE.getnFolioReintegro() + "'" + ",";
                psInsertDetalle.setInt(2, Integer.parseInt(rd.getnDocRenglon().replace(".0", "")));
                buf = buf + "'" + Integer.parseInt(rd.getnDocRenglon().replace(".0", "")) + "'" + ",";
                psInsertDetalle.setString(3, rd.getnSIAFF());
                buf = buf + "'" + rd.getnSIAFF() + "'" + ",";
                psInsertDetalle.setString(4, rd.getSecCLC());
                buf = buf + "'" + rd.getSecCLC() + "'" + ",";
                psInsertDetalle.setString(5, rd.getcEvento());
                buf = buf + "'" + rd.getcEvento() + "'" + ",";
                psInsertDetalle.setString(6, rd.getEP());
                buf = buf + "'" + rd.getEP() + "'" + ",";
                psInsertDetalle.setDouble(7, rd.getmImporteCLC());
                buf = buf + "'" + rd.getmImporteCLC() + "'" + ",";
                psInsertDetalle.setDouble(8, rd.getmImporteCLC());
                buf = buf + "'" + rd.getmImporteCLC() + "'" + ",";
                psInsertDetalle.setDouble(9, rd.getmImporteCLC() * -1);
                buf = buf + "'" + rd.getmImporteCLC() * -1 + "'" + ",";
                psInsertDetalle.setInt(10, rd.getMes());
                buf = buf + "'" + rd.getMes() + "'" + ",";
                psInsertDetalle.setString(11, cCentroContable);
                buf = buf + "'" + cCentroContable + "'" + ",";
                psInsertDetalle.setString(12, rd.getcPartida());
                buf = buf + "'" + rd.getcPartida() + "'" + ",";
                psInsertDetalle.setString(13, "");
                buf = buf + "''" + ",";
                psInsertDetalle.setString(14, rd.getRfc());
                buf = buf + "'" + rd.getRfc() + "'" + ",";
                psInsertDetalle.setString(15, rd.getRenglonPagado());
                buf = buf + "'" + rd.getRenglonPagado() + "'" + ",";
                psInsertDetalle.setString(16, rd.getFolioDependenciaSicop());
                buf = buf + "'" + rd.getFolioDependenciaSicop() + "'" + ",";
                psInsertDetalle.setString(17, rd.getAlm());
                buf = buf + "'" + rd.getAlm() + "'" + ")";
                System.out.println(buf);
                psInsertDetalle.addBatch();
            }
            psInsertDetalle.executeBatch();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            throw e;
        } finally {
            CloseObject.closeObject(psInsertDetalle, false);
        }
    }

    public static boolean validaCatMovimiento(Connection conn, String texto) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        boolean existe = false;
        try {
            pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoMvto with(nolock) WHERE tipo = ?");
            pstm.setString(1, texto);
            res = pstm.executeQuery();
            if (res.next()) {
                if (res.getInt(1) > 0)
                    existe = true;
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (res != null) {
                res.close();
            }
        }
        return existe;
    }

    public static boolean validaCatTipoAviso(Connection conn, String texto) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        boolean existe = false;
        try {
            pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoTipoCausaAviso with(nolock) WHERE cTipoCausaAviso = ?");
            pstm.setString(1, texto);
            res = pstm.executeQuery();
            if (res.next()) {
                if (res.getInt(1) > 0)
                    existe = true;
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (res != null) {
                res.close();
            }
        }
        return existe;
    }

    public static boolean validaCatFormaPago(Connection conn, String texto) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        boolean existe = false;
        try {
            pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoFormaPago with(nolock) WHERE cFormaPago = ?");
            pstm.setString(1, texto);
            res = pstm.executeQuery();
            if (res.next()) {
                if (res.getInt(1) > 0)
                    existe = true;
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (res != null) {
                res.close();
            }
        }
        return existe;
    }

    public static boolean validaCatCausaAviso(Connection conn, String texto) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        boolean existe = false;
        try {
            pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoCausaAviso with(nolock) WHERE cCausaAviso = ?");
            pstm.setString(1, texto);
            res = pstm.executeQuery();
            if (res.next()) {
                if (res.getInt(1) > 0)
                    existe = true;
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (res != null) {
                res.close();
            }
        }
        return existe;
    }

    public static ReintegroDetalleMil datosSICOPMil(Connection conn, String fSIAFF, String EP, String folio, String movimiento, String concepto) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        ReintegroDetalleMil mil = new ReintegroDetalleMil();
        try {
            pstm = conn.prepareStatement("SELECT * FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=? and CONC_MOV_50=? and TCONC_49=?");
            pstm.setString(1, fSIAFF);
            pstm.setString(2, EP);
            pstm.setString(3, movimiento);
            pstm.setString(4, concepto);
            res = pstm.executeQuery();
            if (res.next()) {
                mil.setSecCLC(res.getString("SEC"));
                mil.setFolioDependenciaSicop("NCLC_43");
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (res != null) {
                res.close();
            }
        }
        return mil;
    }

    public static int getRenglonPagadoMil(Connection conn, String caNoContrarrecibo, String EP, int cMes, int folio, String concepto, int movimiento) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int retval = -1;
        try {
            //pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon not in(SELECT nRenglonPagado FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?) order by pd.mImporteNeto");
            pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=?  and ID_TIPO_MOVIMIENTO=? and ID_TIPO_CONCEPTO=?");
            pstmnt.setString(1, caNoContrarrecibo);
            pstmnt.setString(2, EP);
            pstmnt.setInt(3, cMes);
            pstmnt.setInt(4, movimiento);
            pstmnt.setString(5, concepto);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retval = rs.getInt(1);
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            try {
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return retval;
    }

    public static String getALM(Connection conn, String caNoContrarrecibo, String EP, int cMes, int folio, int renglon) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String retval = "";
        try {
            //pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon not in(SELECT nRenglonPagado FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?) order by pd.mImporteNeto");
            pstmnt = conn.prepareStatement("SELECT ALM FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon=?");
            pstmnt.setString(1, caNoContrarrecibo);
            pstmnt.setString(2, EP);
            pstmnt.setInt(3, cMes);
            pstmnt.setInt(4, renglon);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retval = rs.getString(1);
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            try {
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return retval;
    }

    public static String getCCNormal(Connection conn, String caNoContrarrecibo, String EP, int cMes, int folio, int renglon) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String retval = "";
        try {
            //pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon not in(SELECT nRenglonPagado FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?) order by pd.mImporteNeto");
            //pstmnt = conn.prepareStatement("SELECT  pd.cCentroContable FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock),tNOMINACLCEncabezado ne with(nolock), tNOMINACLCDetalle nd with(nolock) WHERE pe.caNoContrarrecibo=? and ne.caNoContrarreciboCLC=? and pd.EP=? and nd.EP=? and pd.nMes=? and nd.cMes=?  and nd.caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and nd.nDocRenglonCLC=pd.nDocRenglon and nd.nFolioNOMINACLC=ne.nFolioNOMINACLC");
            pstmnt = conn.prepareStatement("SELECT  pd.cCentroContable FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE pe.caNoContrarrecibo=? and pd.EP=? and pd.nMes=? and pd.nDocRenglon=? and pd.nFolioPagado=pe.nFolioPagado");
            pstmnt.setString(1, caNoContrarrecibo);
            pstmnt.setString(2, EP);
            pstmnt.setInt(3, cMes);
            pstmnt.setInt(4, renglon);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retval = rs.getString(1);
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            try {
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return retval;
    }

    public static boolean getTipoCLC(Connection conn, int folioCLC) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        boolean retval = false;
        try {
            //pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon not in(SELECT nRenglonPagado FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?) order by pd.mImporteNeto");
            pstmnt = conn.prepareStatement("select TIPO_CLC from CLC_SIAFF_ENC with(nolock), tReintegroDetalleMil with(nolock) where FOLIO_CLC=tReintegroDetalleMil.noCLC and tReintegroDetalleMil.nFolioReintegroMil=?");
            pstmnt.setInt(1, folioCLC);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                if ("COMPENSADA".equals(rs.getString(1)))
                    retval = true;
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            try {
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return retval;
    }

    public static String getListaCorreos(Connection conn, Caso c) throws SQLException {
        String to = "";
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String sSQL = "select distinct u_email from CG_USUARIO where U_LOGIN in (select distinct(B_CO_RESPONSABLE_EJEC) from CG_BITACORA where B_C_FOLIO=?)";
        try {
            pstmnt = conn.prepareStatement(sSQL);
            if (c != null && c.getFolio() != null)
                pstmnt.setString(1, c.getFolio());
            else
                //no encontrara nada, solo es para que corra el qry
                pstmnt.setString(1, "BXX");
            rs = pstmnt.executeQuery();
            boolean enviar = false;
            while (rs.next()) {
                to += (enviar ? ";" : "") + rs.getString(1);
                enviar = true;
            }
        } finally {
            try {
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return to;
    }

    public static String getCorreoRevisor(Connection conn, Caso c) throws SQLException {
        String to = "";
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String sSQL = "select top(1) u_email from CG_USUARIO where U_LOGIN in (select distinct(B_CO_RESPONSABLE_EJEC) from CG_BITACORA where B_C_FOLIO=? and B_ID_OPER=2)";
        try {
            pstmnt = conn.prepareStatement(sSQL);
            if (c != null && c.getFolio() != null)
                pstmnt.setString(1, c.getFolio());
            else
                //no encontrara nada, solo es para que corra el qry
                pstmnt.setString(1, "BXX");
            rs = pstmnt.executeQuery();
            boolean enviar = false;
            if (rs.next()) {
                to = (enviar ? ";" : "") + rs.getString(1);
                enviar = true;
            }
        } finally {
            try {
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return to;
    }

    /**
     * **********************************************************************************************************
     */
    public static String getCorreoAutorizador(Connection conn, String uLogin) throws SQLException {
        String to = "";
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String sSQL = "select top(1) u_email from CG_USUARIO where U_LOGIN = ?";
        try {
            pstmnt = conn.prepareStatement(sSQL);
            pstmnt.setString(1, uLogin);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                to = rs.getString(1);
            }
        } finally {
            try {
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return to;
    }

    /**
     * ************************************************************************************************************
     */
    public static boolean actualizaFechaAplicacion(Connection conn, int folio, String fAplicacion) throws SQLException, ParseException {
        PreparedStatement pstmntUp = null;
        boolean retval = false;
        try {
            pstmntUp = conn.prepareStatement("UPDATE CTRL_DOC..tRefasEncabezado SET fAplicacion=? WHERE nFolioRefas=?");
            pstmntUp.setString(1, fAplicacion);
            pstmntUp.setInt(2, folio);
            retval = pstmntUp.execute();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmntUp != null)
                pstmntUp.close();
            pstmntUp = null;
        }
        return retval;
    }

    public static ReintegroDetalle getReinDetSIAFFSICOP(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        ReintegroDetalle rd = null;
        try {
            pstm = conn.prepareStatement("SELECT distinct noCLC ,nFolioDependencia FROM CTRL_DOC..tRefasDetalle with(nolock) WHERE nFolioRefas = ?");
            pstm.setInt(1, folio);
            res = pstm.executeQuery();
            while (res.next()) {
                rd = new ReintegroDetalle();
                rd.setNoCLC(res.getInt("noCLC"));
                rd.setnFolioDependencia(res.getString("nFolioDependencia"));
            }
        } finally {
            try {
                if (res != null) {
                    res.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return rd;
    }

    public static boolean restaRemante(ReintegroEncabezadoMil refa, ArrayList<ReintegroDetalleMil> refasList, Connection conn, String ejercicio, String tipo) throws SQLException {
        PreparedStatement pstmntUp = null;
        boolean retval = false;
        double remanente = 0;
        int folio = refa.getnFolioReintegro();
        try {
            for (Iterator iterator = refasList.iterator(); iterator.hasNext(); ) {
                ReintegroDetalleMil reintegroDetalleMil = (ReintegroDetalleMil) iterator.next();
                String clc = reintegroDetalleMil.getnSIAFF();
                pstmntUp = conn.prepareStatement("UPDATE CTRL_DOC.. t_Refas" + ejercicio + " SET REMANENTE_" + tipo + "=? WHERE CLC=? AND CLAVE_SIAFF=?");
                remanente = Double.parseDouble(getRetornoRemanente(conn, clc, ejercicio, folio, tipo, reintegroDetalleMil.getEP(), "-"));
                pstmntUp.setDouble(1, remanente);
                pstmntUp.setInt(2, Integer.parseInt(clc));
                pstmntUp.setString(3, reintegroDetalleMil.getEP());
                retval = pstmntUp.execute();
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

    public static String getResiduoRemante(Connection conn, String clc, String ejercicio, int folio, String tipo) throws SQLException {
        PreparedStatement pstmntUp = null;
        ResultSet rs = null;
        String remanente = "";
        String query = "select r.REMANENTE_TEMPORAL + rd.mImporteNegativo as resta from CTRL_DOC..tRefasDetalle rd inner join " + " CTRL_DOC..t_Refas2013 r on rd.noCLC=r.CLC and rd.EP=r.CLAVE_SIAFF  where nFolioRefas=" + folio;
        try {
            pstmntUp = conn.prepareStatement(query);
            rs = pstmntUp.executeQuery();
            while (rs.next()) {
                remanente = rs.getString("resta");
            }
        } finally {
            try {
                if (pstmntUp != null) {
                    pstmntUp.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return remanente;
    }

    public static double getRemanenteValido(Connection conn, String EP, String ejercicio, String clc) throws SQLException {
        ResultSet rs = null;
        double cuantos = 0;
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("SELECT REMANENTE_TEMPORAL FROM CTRL_DOC..t_Refas" + ejercicio + " with(nolock) WHERE CLAVE_SIAFF=? AND CLC =?");
            pstm.setString(1, EP.trim());
            pstm.setString(2, clc.trim());
            rs = pstm.executeQuery();
            if (rs.next()) {
                cuantos = rs.getDouble("REMANENTE_TEMPORAL");
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
        return cuantos;
    }

    public static Date getFechaAct(int folio, Connection conn) throws SQLException {
        ResultSet rs = null;
        Date date = null;
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("SELECT fUltActividad FROM CTRL_DOC..tRefasEncabezado with(nolock) WHERE nFolioRefas=?");
            pstm.setInt(1, folio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                date = rs.getDate(1);
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

    public static Date getFechaApli(int folio, Connection conn) throws SQLException {
        ResultSet rs = null;
        Date date = null;
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("SELECT fAcredit FROM CTRL_DOC..tRefasEncabezado with(nolock) WHERE nFolioRefas=?");
            pstm.setInt(1, folio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                date = rs.getDate(1);
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

    public static void updateFechaAct(int folio, Connection conn) throws SQLException {
        PreparedStatement pstmntUp = null;
        Calendar cal = Calendar.getInstance();
        Date fecha = new Date(cal.getTimeInMillis());
        try {
            pstmntUp = conn.prepareStatement("UPDATE CTRL_DOC.. tRefasEncabezado SET fUltActividad=? WHERE nFolioRefas=? ");
            pstmntUp.setDate(1, fecha);
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

    public static ReintegroEncabezado getRefasEncabezado(Connection conn, int nFolio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        ReintegroEncabezado re = new ReintegroEncabezado();
        try {
            pstm = conn.prepareStatement("SELECT * FROM CTRL_DOC..tRefasEncabezado with(nolock) WHERE nFolioRefas = ?");
            pstm.setInt(1, nFolio);
            res = pstm.executeQuery();
            if (res.next()) {
                re = new ReintegroEncabezado();
                re.setfSolicitud(res.getString("fSolicitud"));
                re.setcTipoReintegro(res.getString("cTipoRefas"));
                re.setfAplicacion(res.getString("fAplicacion"));
                re.setcRamo(res.getString("cRamo"));
                re.setcUnidadResponsable(res.getString("cUnidadResponsable"));
                re.setcUnidadResponsableContable(res.getString("cUnidadResponsableContable"));
                re.setcDocumentoHaplicado(res.getString("cDocumentoHaplicado"));
                re.setnFolioPoliza(res.getInt("nFolioPoliza"));
                re.setcTipoPoliza(res.getString("cTipoPoliza"));
                re.setaEjercicioFiscal(res.getString("aEjercicioFiscal"));
                re.setObservaciones(res.getString("observaciones"));
                re.setConcepto(res.getString("concepto"));
                re.setU_login(res.getString("U_LOGIN"));
                re.setfCancelacion(res.getString("fCancelacion"));
                re.setfExpiracion(res.getString("fExpiracion"));
                re.setfAcreditacion(res.getString("fAcredit"));
                re.setFormaDePago(res.getInt("formaDePago"));
                re.setClvRastreo(res.getString("clvRastreo"));
                re.setFichaDeposito(res.getString("fichaDeposito"));
                re.setClvBanco(res.getString("clvBanco"));
                re.setCuentaBancaria(res.getString("cuentaBancaria"));
                re.setLc(res.getString("lc"));
                DecimalFormat formatter = new DecimalFormat("###,###.##");
                re.setImporteLC(formatter.format(res.getDouble("importeLC")));
                re.setCausaAviso(res.getInt("causaAviso"));
                re.setTipoAviso(res.getInt("tipoAviso"));
                re.setMovimiento(res.getString("mvto"));
                re.setEjercicioRefas(res.getString("ejercicioRefas"));
                re.setnFolioTramiteSiaff(res.getString("nFolioTramiteSiaff"));
                re.setnFolioTramiteSicop(res.getString("nFolioTramiteSicop"));
            }
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (res != null) {
                    res.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return re;
    }

    public static ReintegroEncabezado getRefasEncabezado(int nFolio) throws SQLException {
        ResultSet res = null;
        RefasBusinessLogic ges = new RefasBusinessLogic(null);
        Connection conn = null;
        PreparedStatement pstm = null;
        ReintegroEncabezado re = new ReintegroEncabezado();
        try {
            conn = ges.getConnection();
            pstm = conn.prepareStatement("SELECT * FROM CTRL_DOC..tRefasEncabezado with(nolock) WHERE nFolioRefas = ?");
            pstm.setInt(1, nFolio);
            res = pstm.executeQuery();
            if (res.next()) {
                re = new ReintegroEncabezado();
                re.setfSolicitud(res.getString("fSolicitud"));
                re.setcTipoReintegro(res.getString("cTipoRefas"));
                re.setfAplicacion(res.getString("fAplicacion"));
                re.setcRamo(res.getString("cRamo"));
                re.setcUnidadResponsable(res.getString("cUnidadResponsable"));
                re.setcUnidadResponsableContable(res.getString("cUnidadResponsableContable"));
                re.setcDocumentoHaplicado(res.getString("cDocumentoHaplicado"));
                re.setnFolioPoliza(res.getInt("nFolioPoliza"));
                re.setcTipoPoliza(res.getString("cTipoPoliza"));
                re.setaEjercicioFiscal(res.getString("aEjercicioFiscal"));
                re.setObservaciones(res.getString("observaciones"));
                re.setConcepto(res.getString("concepto"));
                re.setU_login(res.getString("U_LOGIN"));
                re.setfCancelacion(res.getString("fCancelacion"));
                re.setfExpiracion(res.getString("fExpiracion"));
                re.setfAcreditacion(res.getString("fAcredit"));
                re.setFormaDePago(res.getInt("formaDePago"));
                re.setClvRastreo(res.getString("clvRastreo"));
                re.setFichaDeposito(res.getString("fichaDeposito"));
                re.setClvBanco(res.getString("clvBanco"));
                re.setCuentaBancaria(res.getString("cuentaBancaria"));
                re.setLc(res.getString("lc"));
                DecimalFormat formatter = new DecimalFormat("###,###.##");
                re.setImporteLC(formatter.format(res.getDouble("importeLC")));
                re.setCausaAviso(res.getInt("causaAviso"));
                re.setTipoAviso(res.getInt("tipoAviso"));
                re.setMovimiento(res.getString("mvto"));
                re.setEjercicioRefas(res.getString("ejercicioRefas"));
            }
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (res != null) {
                    res.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return re;
    }

    public static ArrayList<ReintegroDetalle> getRefasLayout(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        ResultSet res2 = null, res3 = null, res4 = null;
        PreparedStatement pstm = null;
        Statement stmt = null;
        String sCBEN = "";
        ArrayList<ReintegroDetalle> detalles = new ArrayList<ReintegroDetalle>();
        ReintegroDetalle rd;
        /**
         * *************************************************************************************************************
         */
        String SQLMain = "select  ROW_NUMBER() OVER (ORDER BY e.cProgramaPresupuestario desc) AS nDocRenglon " + ",e.cRamoEP " + ", e.cUnidadResponsableEP " + ", e.aEjercicioFiscal " + ", e.cGrupoFuncional " + ", e.cFuncion  " + ", e.cSubFuncion " + ", e.cProgramaGeneral " + ", e.cActividadInstitucional " + ", e.cProgramaPresupuestario " + ", substring(e.cPartida,1,1) as cCapitulo " + ", substring(e.cPartida,2,1) as cConcepto " + ", substring(e.cPartida,3,1) as cPartida " + ", substring(e.cPartida,4,2) as cPartidaEspecifica " + ", e.cTipoGasto " + ", e.cFuenteFinanciamiento " + ", e.cEntidadFederativa " + ", e.cCartera " + ", left(replicate('0', 7)+e.cUnidadEjecutora,10) as cUnidadEjecutora " + ", substring(e.cUnidadNorativa,2,2) as cUnidadNormativa " + ", '0','0','0','0','0' " + ", CONVERT(VARCHAR,(SUM(d.mImporte))) as mImporte " + ", d.cMes " + ", CASE WHEN NCOM_15 is null OR NCOM_15='' THEN '' ELSE REPLICATE('0',6-LEN(LTRIM(NCOM_15)))+LTRIM(CAST(NCOM_15 AS VARCHAR(6))) END as NCOM_15 " + ", '' as cFillRellen2  " + ", isnull(NOIF_18,0) as sol_oli " + ", TPAG_117 " + ", CASE WHEN rclc.CONC_MOV_50 is null OR rclc.CONC_MOV_50='' THEN '' ELSE REPLICATE('0',3-LEN(LTRIM(rclc.CONC_MOV_50)))+LTRIM(CAST(rclc.CONC_MOV_50 AS VARCHAR(3))) END as ID_TIPO_CONCEPTO " + ", 0 as retencion_isr " + ", '0' " + ", d.cEvento " + ", p.nFolioSICOP " + ",d.EP " + ",d.secCLC " + ",SPAG_176 " + ",TCONC_49 " + ",isnull(NRES_17,'') as NRES_17 " + ",case (SELECT top(1) sCBEN " + "       FROM tLayoutsCreadosRelacionGastosHeader a with(nolock) " + "	 inner join tReintegroDetalle b WITH(NOLOCK) on  a.sNoContrarrecibo=b.cxp " + "	inner join tPagadoEncabezado c WITH(NOLOCK) on  c.caNoContrarrecibo=a.sNoContrarrecibo " + "	 where b.nFolioReintegro=d.nFolioReintegro) " + "	when null then b.CBEN " + "	else (SELECT top(1) sCBEN " + "	 FROM tLayoutsCreadosRelacionGastosHeader a with(nolock)" + "	 inner join tReintegroDetalle b WITH(NOLOCK) on  a.sNoContrarrecibo=b.cxp" + "	inner join tPagadoEncabezado c WITH(NOLOCK) on  c.caNoContrarrecibo=a.sNoContrarrecibo" + "  where b.nFolioReintegro=d.nFolioReintegro)	end as CBENE " + "from tReintegroDetalle d with (nolock), tReintegroEncabezado r with (nolock), tCatalogoEP e with (nolock), tPagadoEncabezado P with (nolock), tPagadoDetalle dp  with (nolock), tBeneficiario b with (nolock), vReintegrosCLCSICOP rclc with(nolock) " + " WHERE d.nFolioReintegro =  r.nFolioReintegro and d.EP =  e.EP and d.cxp = P.caNoContrarrecibo and p.nFolioPagado = dp.nFolioPagado and d.nRenglonPagado = dp.nDocRenglon and " + "dp.RFC = b.dRFC and rclc.EP=d.EP and rclc.MES_149 = d.cMes and rclc.FolioSIAFF=d.noCLC and rclc.FolioSIAFF=p.nFolioSIAFF and rclc.SEC=d.secCLC and d.nFolioReintegro=? and r.nFolioReintegro=? " + "group by " + "e.cRamoEP " + ", e.cUnidadResponsableEP " + ", e.aEjercicioFiscal " + ", e.cGrupoFuncional " + ", e.cFuncion " + ", e.cSubFuncion " + ", e.cProgramaGeneral " + ", e.cActividadInstitucional " + ", e.cProgramaPresupuestario " + ", e.cPartida " + ", e.cTipoGasto " + ", e.cFuenteFinanciamiento " + ", e.cEntidadFederativa " + ", e.cCartera " + ", e.cUnidadEjecutora " + ", e.cUnidadNorativa " + ", d.cMes " + ", d.nFolioReintegro " + ", rclc.NCOM_15 " + ", p.cTipoPago  " + ", rclc.NOIF_18 " + ", rclc.TPAG_117 " + ", rclc.CONC_MOV_50 " + ", d.cEvento " + ", p.nFolioSICOP " + ", d.EP " + ", d.secCLC " + ", rclc.SPAG_176 " + ", rclc.TCONC_49 " + ", rclc.NRES_17 " + "order by p.nFolioSICOP ";
        try {
            pstm = conn.prepareStatement(SQLMain);
            /**
             * ******************************************************************************************************************
             */
            /*pstm = conn.prepareStatement("select distinct d.nDocRenglon, e.cRamoEP, e.cUnidadResponsableEP, e.aEjercicioFiscal, e.cGrupoFuncional, e.cFuncion " +
					", e.cSubFuncion, e.cProgramaGeneral, e.cActividadInstitucional, e.cProgramaPresupuestario, substring(e.cPartida,1,1) as cCapitulo " +
					", substring(e.cPartida,2,1) as cConcepto, substring(e.cPartida,3,1) as cPartida, substring(e.cPartida,4,2) as cPartidaEspecifica " +
					", e.cTipoGasto, e.cFuenteFinanciamiento, e.cEntidadFederativa, e.cCartera, left(replicate('0', 7)+e.cUnidadEjecutora,10) as cUnidadEjecutora " +
					", substring(e.cUnidadNorativa,2,2) as cUnidadNormativa, '0','0','0','0','0', sum(d.mImporte) as mImporte, d.cMes, CASE WHEN NCOM_15 is null OR NCOM_15='' THEN '' ELSE REPLICATE('0',6-LEN(LTRIM(NCOM_15)))+LTRIM(CAST(NCOM_15 AS VARCHAR(6))) END as NCOM_15 "+
					", CASE WHEN p.cTipoPago='RELACIONGASTOS' THEN "+ sCBEN +" ELSE b.CBEN END as CBEN, '' as cFillRellen2 , isnull(NOIF_18,0) as sol_oli, TPAG_117, CASE WHEN rclc.CONC_MOV_50 is null OR rclc.CONC_MOV_50='' THEN '' ELSE REPLICATE('0',3-LEN(LTRIM(rclc.CONC_MOV_50)))+LTRIM(CAST(rclc.CONC_MOV_50 AS VARCHAR(3))) END as ID_TIPO_CONCEPTO "+
					", 0 as retencion_isr, '0', d.cEvento, p.nFolioSICOP,d.EP,d.secCLC,SPAG_176,TCONC_49,isnull(NRES_17,'') as NRES_17 "+ 
					" from tReintegroDetalle d with (nolock), tReintegroEncabezado r with (nolock), tCatalogoEP e with (nolock), tPagadoEncabezado P with (nolock), tPagadoDetalle dp  with (nolock), tBeneficiario b with (nolock), vReintegrosCLCSICOP rclc with(nolock)" +
					" WHERE d.nFolioReintegro =  r.nFolioReintegro" +
					//" and d.EP =  e.EP and d.cxp = P.caNoContrarrecibo and p.nFolioPagado = dp.nFolioPagado and dp.RFC = b.dRFC and rclc.EP=d.EP and rclc.EP=e.EP and rclc.caNoContrarrecibo=d.cxp and rclc.caNoContrarrecibo=p.caNoContrarrecibo and rclc.SEC=d.secCLC and d.nFolioReintegro=? and r.nFolioReintegro=?");
			" and d.EP =  e.EP and d.cxp = P.caNoContrarrecibo and p.nFolioPagado = dp.nFolioPagado and dp.RFC = b.dRFC and rclc.EP=d.EP and rclc.EP=e.EP and rclc.FolioSIAFF=d.noCLC and rclc.FolioSIAFF=p.nFolioSIAFF and rclc.SEC=d.secCLC and d.nFolioReintegro=? and r.nFolioReintegro=?" +*/
            //);
            pstm.setInt(1, folio);
            pstm.setInt(2, folio);
            res = pstm.executeQuery();
            int numRows = 0;
            while (res.next()) {
                numRows++;
                /**
                 * *****************************************************************************************
                 */
                String PQuery = "SELECT top(1) sCBEN " + " FROM tLayoutsCreadosRelacionGastosHeader a with(nolock) " + " inner join tReintegroDetalle b WITH(NOLOCK) on  a.sNoContrarrecibo=b.cxp " + " inner join tPagadoEncabezado c WITH(NOLOCK) on  c.caNoContrarrecibo=a.sNoContrarrecibo " + " where c.cTipoPago='RELACIONGASTOS' and b.nFolioReintegro=" + folio;
                stmt = conn.createStatement();
                res2 = stmt.executeQuery(PQuery);
                if (res2.next())
                    sCBEN = res2.getString(1);
                else {
                    /// esto no debiera pasar si no se encontr esta mal una parte del sitema
                    System.out.println("*************************************Error al tratar de encontrar el valor CBEN****************************************");
                    PQuery = "SELECT top(1) sCBEN " + " FROM tLayoutsCreadosRelacionGastosHeader a with(nolock) " + " inner join tReintegroDetalle b WITH(NOLOCK) on  a.sNoContrarrecibo=b.cxp " + " inner join tPagadoEncabezado c WITH(NOLOCK) on  c.caNoContrarrecibo=a.sNoContrarrecibo " + " where b.nFolioReintegro=" + folio;
                    stmt = conn.createStatement();
                    res3 = stmt.executeQuery(PQuery);
                    if (res3.next())
                        sCBEN = res3.getString(1);
                    else {
                        System.out.println("*************************************Error al tratar de encontrar el valor CBEN****************************************");
                        PQuery = " SELECT top(1) CBEN " + " FROM tReintegroDetalle a with(nolock) " + " inner join tPagadoEncabezado c WITH(NOLOCK) on  c.caNoContrarrecibo=a.cxp " + " INNER JOIN TBENEFICIARIO B WITH(NOLOCK) ON  A.RFC = B.DRFC " + " where a.nFolioReintegro =" + folio;
                        stmt = conn.createStatement();
                        res4 = stmt.executeQuery(PQuery);
                        if (res4.next())
                            sCBEN = res4.getString(1);
                        else {
                            System.out.println("*************************************Error al tratar de encontrar el valor CBEN  del detalle****************************************");
                            sCBEN = res.getString("CBENE");
                        }
                    }
                }
                /**
                 * *********************************************************************************************
                 */
                rd = new ReintegroDetalle();
                //rd.setBeneficiario(res.getString("CBEN"));
                rd.setBeneficiario(sCBEN);
                rd.setnDocRenglon(res.getInt("nDocRenglon"));
                rd.setTipoCon(res.getString("ID_TIPO_CONCEPTO"));
                rd.setSolOli(res.getInt("sol_oli"));
                rd.setIsr(res.getInt("retencion_isr"));
                rd.setClcSicop(res.getString("nFolioSICOP"));
                //lo puse en nCompromiso porque ese no lo uso y cUnidadNormativa si lo tengo que recuperar, es temporal
                rd.setnCompromiso(res.getString("cUnidadNormativa"));
                //nPartida tendrá el importe que se hace varchar para que se muestre en buen formato
                rd.setnPartida(res.getString("mImporte"));
                rd.setMes(res.getInt("cMes"));
                rd.setEP(res.getString("EP"));
                rd.setSecCLC(res.getInt("secCLC"));
                //Sirve para alamacenar temporalmente el ncom_15
                rd.setCxp(res.getString("NCOM_15"));
                rd.setTpag(res.getString("TPAG_117"));
                rd.setTipoDeCon(res.getString("TCONC_49"));
                //SIrve para almacenar temporalmente spag_176
                rd.setSuficiencia(res.getString("SPAG_176"));
                rd.setNres(res.getString("NRES_17"));
                detalles.add(rd);
            }
            if (numRows == 0) {
                //si no hubo registros buscamos en capitulo mil
                pstm = conn.prepareStatement(//" and d.EP =  e.EP and d.cxp = P.caNoContrarrecibo and p.nFolioPagado = dp.nFolioPagado and dp.RFC = b.sCodigoEntidad  and rclc.EP=d.EP and rclc.EP=e.EP and rclc.caNoContrarrecibo=d.cxp and rclc.caNoContrarrecibo=p.caNoContrarrecibo and rclc.SEC=d.secCLC and d.nFolioReintegro=? and r.nFolioReintegro=?");
                "select distinct d.nDocRenglon, e.cRamoEP, e.cUnidadResponsableEP, e.aEjercicioFiscal, e.cGrupoFuncional, e.cFuncion " + ", e.cSubFuncion, e.cProgramaGeneral, e.cActividadInstitucional, e.cProgramaPresupuestario, substring(e.cPartida,1,1) as cCapitulo " + ", substring(e.cPartida,2,1) as cConcepto, substring(e.cPartida,3,1) as cPartida, substring(e.cPartida,4,2) as cPartidaEspecifica " + ", e.cTipoGasto, e.cFuenteFinanciamiento, e.cEntidadFederativa, e.cCartera, left(replicate('0', 7)+e.cUnidadEjecutora,10) as cUnidadEjecutora " + ", substring(e.cUnidadNorativa,2,2) as cUnidadNormativa, '0','0','0','0','0', CONVERT(VARCHAR,d.mImporte) as mImporte, d.cMes, CASE WHEN NCOM_15 is null OR NCOM_15='' THEN '' ELSE REPLICATE('0',6-LEN(LTRIM(NCOM_15)))+LTRIM(CAST(NCOM_15 AS VARCHAR(6))) END as NCOM_15 " + ", b.sSicop, '' as cFillRellen2 , isnull(NOIF_18,0) as sol_oli, TPAG_117, rclc.CONC_MOV_50 as ID_TIPO_CONCEPTO " + ", 0 as retencion_isr, '0', d.cEvento, p.nFolioSICOP,d.EP,d.secCLC,SPAG_176,TCONC_49,isnull(NRES_17,'') as NRES_17 " + " from tReintegroDetalle d with (nolock), tReintegroEncabezado r with (nolock), tCatalogoEP e with (nolock), tPagadoEncabezado P with (nolock), tPagadoDetalle dp  with (nolock), tBeneficiarioCapituloMil b with (nolock), vReintegrosCLCSICOP rclc with(nolock)" + " WHERE d.nFolioReintegro =  r.nFolioReintegro" + " and d.EP =  e.EP and d.cxp = P.caNoContrarrecibo and p.nFolioPagado = dp.nFolioPagado and dp.RFC = b.sCodigoEntidad  and rclc.EP=d.EP and rclc.EP=e.EP and rclc.FolioSIAFF=d.noCLC and rclc.FolioSIAFF=p.nFolioSIAFF and rclc.SEC=d.secCLC and d.nFolioReintegro=? and r.nFolioReintegro=?");
                pstm.setInt(1, folio);
                pstm.setInt(2, folio);
                res = pstm.executeQuery();
                while (res.next()) {
                    rd = new ReintegroDetalle();
                    rd.setBeneficiario(res.getString("sSicop"));
                    rd.setnDocRenglon(res.getInt("nDocRenglon"));
                    rd.setTipoCon(res.getString("ID_TIPO_CONCEPTO"));
                    rd.setSolOli(res.getInt("sol_oli"));
                    rd.setIsr(res.getInt("retencion_isr"));
                    rd.setClcSicop(res.getString("nFolioSICOP"));
                    //lo puse en nCompromiso porque ese no lo uso y cUnidadNormativa si lo tengo que recuperar, es temporal
                    rd.setnCompromiso(res.getString("cUnidadNormativa"));
                    //nPartida tendrá el importe que se hace varchar para que se muestre en buen formato
                    rd.setnPartida(res.getString("mImporte"));
                    rd.setMes(res.getInt("cMes"));
                    rd.setEP(res.getString("EP"));
                    rd.setSecCLC(res.getInt("secCLC"));
                    //Sirve para alamacenar temporalmente el ncom_15
                    rd.setCxp(res.getString("NCOM_15"));
                    rd.setTpag(res.getString("TPAG_117"));
                    rd.setTipoDeCon(res.getString("TCONC_49"));
                    //SIrve para almacenar temporalmente spag_176
                    rd.setSuficiencia(res.getString("SPAG_176"));
                    rd.setNres(res.getString("NRES_17"));
                    detalles.add(rd);
                }
            }
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
                if (res4 != null) {
                    res4.close();
                }
                if (res3 != null) {
                    res3.close();
                }
                if (res2 != null) {
                    res2.close();
                }
                if (res != null) {
                    res.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return detalles;
    }

    public static void updateFechaCancelacion(String strfolio, Connection conn) throws SQLException {
        PreparedStatement pstmntUp = null;
        int folio = getFolioRefas(strfolio);
        Calendar cal = Calendar.getInstance();
        Date fecha = new Date(cal.getTimeInMillis());
        try {
            pstmntUp = conn.prepareStatement("UPDATE CTRL_DOC.. tRefasEncabezado SET fCancelacion=?,cDocumentoHaplicado='C' WHERE nFolioRefas=? ");
            pstmntUp.setDate(1, fecha);
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

    public static void updateFechaRevision(int folio, Connection conn) throws SQLException {
        PreparedStatement pstmntUp = null;
        Calendar cal = Calendar.getInstance();
        Date fecha = new Date(cal.getTimeInMillis());
        try {
            pstmntUp = conn.prepareStatement("UPDATE CTRL_DOC.. tRefasEncabezado SET fRevision=? WHERE nFolioRefas=? ");
            pstmntUp.setDate(1, fecha);
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

    public static boolean restaRemante(int folio, ArrayList<ReintegroDetalleMil> refasList, Connection conn, String ejercicio, String tipo) throws SQLException {
        PreparedStatement pstmntUp = null;
        boolean retval = false;
        double remanente = 0;
        try {
            for (Iterator iterator = refasList.iterator(); iterator.hasNext(); ) {
                ReintegroDetalleMil reintegroDetalleMil = (ReintegroDetalleMil) iterator.next();
                String clc = reintegroDetalleMil.getnSIAFF();
                pstmntUp = conn.prepareStatement("UPDATE CTRL_DOC.. t_Refas" + ejercicio + " SET REMANENTE_" + tipo + "=? WHERE CLC=? AND CLAVE_SIAFF=?");
                remanente = Double.parseDouble(getResiduoRemante(conn, clc, ejercicio, folio, tipo));
                pstmntUp.setDouble(1, remanente);
                pstmntUp.setInt(2, Integer.parseInt(clc));
                pstmntUp.setString(3, reintegroDetalleMil.getEP());
                retval = pstmntUp.execute();
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

    public static boolean restaRemanteReintegro(int folio, ArrayList<ReintegroDetalle> refasList, Connection conn, String ejercicio, String tipo, String operacion) throws SQLException {
        PreparedStatement pstmntUp = null;
        boolean retval = false;
        double remanente = 0;
        try {
            for (Iterator iterator = refasList.iterator(); iterator.hasNext(); ) {
                ReintegroDetalle refasDetalle = (ReintegroDetalle) iterator.next();
                int clc = refasDetalle.getNoCLC();
                pstmntUp = conn.prepareStatement("UPDATE CTRL_DOC.. t_Refas" + ejercicio + " SET REMANENTE_" + tipo + "=? WHERE CLC=? AND CLAVE_SIAFF=?");
                remanente = Double.parseDouble(getRetornoRemanente(conn, String.valueOf(clc), ejercicio, folio, tipo, refasDetalle.getEP(), operacion));
                pstmntUp.setDouble(1, remanente);
                pstmntUp.setInt(2, clc);
                pstmntUp.setString(3, refasDetalle.getEP());
                retval = pstmntUp.execute();
            }
        } catch (SQLException s) {
            s.printStackTrace();
            throw s;
        } finally {
            if (pstmntUp != null)
                pstmntUp.close();
            pstmntUp = null;
        }
        return retval;
    }

    public static String getComentarioSicop(String observaciones, String comentario, ReintegroEncabezado re, String folio, Usuario u, ArrayList<ReintegroDetalle> reintegrosDetalle) throws GestionException, SQLException {
        StringBuilder strB = new StringBuilder();
        String respuesta = "";
        strB.append(folio + " ");
        RefasBusinessLogic refasBusinessLogic = new RefasBusinessLogic(null);
        Connection conn = null;
        try {
            conn = refasBusinessLogic.getConnection();
            if (observaciones != null) {
                comentario = comentario.replace(",", "");
                comentario = comentario.toUpperCase();
                strB.append(re.getcUnidadResponsable() + " ");
                strB.append(getUnidadResponsable(re.getcUnidadResponsable(), conn));
                strB.append(" JUSTIFICACION:" + comentario);
                for (int i = 0; i < reintegrosDetalle.size(); i++) {
                    strB.append(" CLC:" + reintegrosDetalle.get(i).getNoCLC());
                    strB.append(" EP:" + reintegrosDetalle.get(i).getEP());
                    strB.append(" IMP:" + reintegrosDetalle.get(i).getmImporteCLC());
                }
                if (strB.length() > 474)
                    strB.setLength(474);
                String[] split = folio.split("-");
                respuesta = strB.toString() + "," + re.getaEjercicioFiscal() + "-" + split[0] + "-" + re.getEjercicioRefas() + "-" + split[1] + "-" + split[2];
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return respuesta;
    }

    public static ArrayList<Map<String, String>> insertaDataProcesoAutomatico(Connection conn, ArrayList<String> arrayLst) throws Exception {
        PreparedStatement pstmnt = null;
        Map<String, String> mapFolios = new HashMap<String, String>();
        Map<String, String> mapFolios2 = new HashMap<String, String>();
        StringBuffer query = new StringBuffer();
        ArrayList<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
        try {
            // checar  cuando viene
            int folio = getFolioRefas((arrayLst.get(18)));
            RefasBusinessLogic businessLogic = new RefasBusinessLogic(null);
            if (folio != -1) {
                ReintegroEncabezado re = businessLogic.getRefasEncabezado(folio);
                boolean ejecutaQuery = false;
                if (arrayLst.get(16).equals("Pagada") && re.getcDocumentoHaplicado().equals("N")) {
                    mapFolios.put(String.valueOf(getFolioRefas(arrayLst.get(18))), "autorizada");
                    mapFolios2.put(getFolioRefasStr(arrayLst.get(18)), "autorizada");
                    query.append("update CTRL_DOC..tRefasEncabezado set fAcredit =?, fAplicacion =?");
                    query.append(",lc ='" + arrayLst.get(15) + "'");
                    query.append(",nFolioTramiteSiaff='" + arrayLst.get(17).replace('.', 'd').split("d")[0] + "'");
                    query.append(",nFolioTramiteSicop='" + arrayLst.get(5).replace('.', 'd').split("d")[0] + "',cDocumentoHaplicado='S'");
                    query.append(" where nFolioRefas=?");
                    pstmnt = conn.prepareStatement(query.toString());
                    String date = arrayLst.get(21).replace("/", "-");
                    String[] splitDate = date.split("-");
                    String dateFinal = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
                    Date dt = Date.valueOf(dateFinal);
                    pstmnt.setDate(1, dt);
                    pstmnt.setDate(2, dt);
                    pstmnt.setInt(3, folio);
                    pstmnt.execute();
                } else if (arrayLst.get(16).equals("Caduca") && re.getcDocumentoHaplicado().equals("N")) {
                    mapFolios.put(String.valueOf(getFolioRefas(arrayLst.get(18))), "caduca");
                    mapFolios2.put(getFolioRefasStr(arrayLst.get(18)), "caduca");
                    query.append("update CTRL_DOC..tRefasEncabezado set fCancelacion =? ");
                    query.append(",nFolioTramiteSiaff='" + arrayLst.get(17) + "'");
                    query.append(",nFolioTramiteSicop='" + arrayLst.get(5) + "',cDocumentoHaplicado='C' ");
                    query.append(" where nFolioRefas=?");
                    pstmnt = conn.prepareStatement(query.toString());
                    String date = arrayLst.get(21).replace("/", "-");
                    String[] splitDate = date.split("-");
                    String dateFinal = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
                    Date dt = Date.valueOf(dateFinal);
                    pstmnt.setDate(1, dt);
                    pstmnt.setInt(2, folio);
                    pstmnt.execute();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        listMap.add(mapFolios);
        listMap.add(mapFolios2);
        return listMap;
    }

    public static int getFolioRefas(String comentario) {
        //REFA-B03-41
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

    public static String getFolioRefasStr(String comentario) {
        //REFA-B03-41
        String resp = "";
        String[] splitComentario = comentario.split(" ");
        try {
            resp = splitComentario[0];
        } catch (Exception e) {
            return resp = "";
        }
        return resp;
    }

    public static void avanzarCasos(Map<String, String> mapFolios, Map m, String prefixPath, String uLogin, Connection conn, HttpServletRequest request) throws Exception {
        Caso srchCase = new Caso();
        for (Map.Entry<String, String> entry : mapFolios.entrySet()) {
            Caso rc = new Caso();
            srchCase.setFolio(entry.getKey());
            srchCase.setIdTC(44);
            rc = CasoManager.select(conn, srchCase);
            RefasBusinessLogic businessLogic = new RefasBusinessLogic(null);
            businessLogic.avanzaCasoAutorizado(rc, m, prefixPath, uLogin, entry.getValue());
        }
    }

    public static void restaRemanenteMap(Map<String, String> mapFolios, Connection conn) throws NumberFormatException, Exception {
        for (Map.Entry<String, String> entry : mapFolios.entrySet()) {
            RefasBusinessLogic businessLogic = new RefasBusinessLogic(null);
            if (entry.getValue().equals("autorizada")) {
                businessLogic.restarRemante(Integer.parseInt(entry.getKey()), "SAI", "-");
                PolizasTrigger polizasTrigger = new PolizasTrigger();
                polizasTrigger.creaPolizaRefas(entry.getKey(), conn);
            } else {
                //Si se cancela se suma el remanente
                businessLogic.restarRemante(Integer.parseInt(entry.getKey()), "TEMPORAL", "+");
            }
        }
    }

    public static boolean retornaRemante(String folio, Connection conn, String tipo) throws SQLException {
        PreparedStatement pstmntUp = null;
        boolean retval = false;
        double retorno = 0;
        int intFolio = getFolioRefas(folio);
        ReintegroEncabezado encabezado = getRefasEncabezado(conn, intFolio);
        ArrayList<ReintegroDetalle> refasList = getRefasDetalle(conn, intFolio);
        String ejercicio = encabezado.getEjercicioRefas();
        try {
            for (Iterator iterator = refasList.iterator(); iterator.hasNext(); ) {
                ReintegroDetalle refasDetalle = (ReintegroDetalle) iterator.next();
                int clc = refasDetalle.getNoCLC();
                pstmntUp = conn.prepareStatement("UPDATE CTRL_DOC.. t_Refas" + ejercicio + " SET REMANENTE_" + tipo + "=? WHERE CLC=? AND CLAVE_SIAFF=?");
                retorno = Double.parseDouble(getRetornoRemanente(conn, String.valueOf(clc), ejercicio, intFolio, tipo, refasDetalle.getEP(), "+"));
                pstmntUp.setDouble(1, retorno);
                pstmntUp.setInt(2, clc);
                pstmntUp.setString(3, refasDetalle.getEP());
                retval = pstmntUp.execute();
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

    public static String getRetornoRemanente(Connection conn, String clc, String ejercicio, int folio, String tipo, String ep, String operacion) throws SQLException {
        PreparedStatement pstmntUp = null;
        ResultSet rs = null;
        String remanente = "";
        try {
            String query = "select r.REMANENTE_" + tipo + " " + operacion + " rd.mImporte as retorno from CTRL_DOC..tRefasDetalle rd inner join " + " CTRL_DOC..t_Refas" + ejercicio + " r on rd.noCLC=r.CLC and rd.EP=r.CLAVE_SIAFF " + " where nFolioRefas=" + folio + " and rd.EP='" + ep + "'" + "and rd.noCLC=" + clc;
            pstmntUp = conn.prepareStatement(query);
            rs = pstmntUp.executeQuery();
            while (rs.next()) {
                remanente = rs.getString("retorno");
            }
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmntUp != null) {
                    pstmntUp.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return remanente;
    }

    public static String getFolioSicop(int folio, String strFolio) throws SQLException {
        PreparedStatement pstmntUp = null;
        ResultSet rs = null;
        String ejercicio = "";
        String ejercicioRefas = "";
        String folio_reintegro_sicop = "";
        Connection conn = null;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            pstmntUp = conn.prepareStatement("select ejercicioRefas,aEjercicioFiscal from  CTRL_DOC..tRefasEncabezado where nFolioRefas = ?");
            pstmntUp.setInt(1, folio);
            rs = pstmntUp.executeQuery();
            while (rs.next()) {
                ejercicio = rs.getString("aEjercicioFiscal");
                ejercicioRefas = rs.getString("ejercicioRefas");
            }
            String[] slitFolio = strFolio.split("-");
            folio_reintegro_sicop = ejercicio + "-" + slitFolio[0] + "-" + ejercicioRefas + "-" + slitFolio[1] + "-" + slitFolio[2];
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmntUp != null) {
                    pstmntUp.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return folio_reintegro_sicop;
    }

    public static ArrayList<String> getUsersRefas(String folio, Connection conn) throws SQLException {
        PreparedStatement pstmntUp = null;
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        ResultSet rs = null;
        ArrayList<String> list = new ArrayList<String>();
        try {
            pstmntUp = conn.prepareStatement("select nFolioRefas,uAutorizacion,cu.U_EMAIL from CTRL_DOC..tRefasEncabezado re with(nolock)" + " inner join cg_usuario cu with(nolock) on re.uAutorizacion = cu.U_LOGIN " + " where nFolioRefas=" + folio + " union" + " select nFolioRefas,uCaptura,cu.U_EMAIL from CTRL_DOC..tRefasEncabezado re with(nolock)" + " inner join cg_usuario cu with(nolock) on re.uCaptura = cu.U_LOGIN " + " where nFolioRefas=" + folio + " union " + " select nFolioRefas,uRevision,cu.U_EMAIL from CTRL_DOC..tRefasEncabezado re with(nolock)" + " inner join cg_usuario cu with(nolock)on re.uRevision = cu.U_LOGIN " + " where nFolioRefas=" + folio);
            rs = pstmntUp.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("U_EMAIL"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                pstmntUp.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return list;
    }

    public static String getUnidadResponsable(String unidad, Connection conn) throws SQLException {
        ResultSet rs = null;
        String descripcion = "";
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("select D_DESCRIPCION from tCatUnidadResponsable with(nolock) where cUnidadResponsable='" + unidad + "'");
            rs = pstm.executeQuery();
            if (rs.next()) {
                descripcion = rs.getString(1);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return descripcion;
    }

    public static Map<String, String> getFoliosDocPendientes() {
        ResultSet rs = null;
        PreparedStatement pstm = null;
        Map<String, String> mapFolios = new HashMap<String, String>();
        Connection conn = null;
        try {
            conn = RefasBusinessLogic.getConnection(GestionInterface.ATT_CONEXION);
            StringBuffer query = new StringBuffer();
            query.append(" SELECT re.nFolioRefas, ");
            query.append("       COUNT(re.nFolioRefas) AS cantArchivos ");
            query.append("FROM CTRL_DOC..tRefasEncabezado re WITH (nolock) ");
            query.append("INNER JOIN ");
            query.append("imx_pagina p WITH (nolock) ");
            query.append("ON p.TITULO_APLICACION='REFAS' ");
            query.append("AND p.id_gabinete=( ");
            query.append("    SELECT ID_GABINETE ");
            query.append("    FROM imx_documento d ");
            query.append("    WHERE d.NOMBRE_DOCUMENTO LIKE 'REFA-%-'+CONVERT( VARCHAR, re.nFolioRefas)) ");
            query.append("INNER JOIN ");
            query.append("imx_documento d WITH (nolock) ");
            query.append("ON d.titulo_aplicacion=p.titulo_aplicacion ");
            query.append("AND d.id_gabinete=p.id_gabinete ");
            query.append("AND d.id_carpeta_padre=p.id_carpeta_padre ");
            query.append("AND d.id_documento=p.id_documento ");
            query.append("INNER JOIN ");
            query.append("imx_volumen v WITH (nolock) ");
            query.append("ON v.volumen=p.volumen ");
            query.append("WHERE re.cDocumentoHaplicado='S' ");
            query.append("  AND d.TITULO_APLICACION='REFAS' ");
            query.append("  AND d.NOMBRE_DOCUMENTO IN('PDF CLC', ");
            query.append("			'Solicitud Linea Captura', ");
            query.append("          'Linea de Captura', ");
            query.append("          'Pago Carga F', ");
            query.append("          'Pago de Refas', ");
            query.append("          'Reporte SICOP', ");
            query.append("          'Reporte SIAFF', ");
            query.append("          'Reporte') ");
            query.append("GROUP BY re.nFolioRefas ");
            pstm = conn.prepareStatement(query.toString());
            rs = pstm.executeQuery();
            while (rs.next()) {
                if (rs.getInt("cantArchivos") < 8)
                    mapFolios.put(rs.getString("nFolioRefas"), "pendiente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return mapFolios;
    }

    public static Map<String, String> getFoliosDocPendientesReintegros() {
        ResultSet rs = null;
        PreparedStatement pstm = null;
        Map<String, String> mapFolios = new HashMap<String, String>();
        Connection conn = null;
        try {
            conn = RefasBusinessLogic.getConnection(GestionInterface.ATT_CONEXION);
            StringBuffer query = new StringBuffer();
            query.append(" SELECT re.nFolioReintegroAut,COUNT(re.nFolioReintegroAut) AS cantArchivos ");
            query.append("FROM dbo.tReintegroAutEncabezado re WITH (nolock) ");
            query.append("INNER JOIN ");
            query.append("imx_pagina p WITH (nolock) ");
            query.append("ON p.TITULO_APLICACION='REINTEGRO' ");
            query.append("AND p.id_gabinete=( ");
            query.append("    SELECT ID_GABINETE ");
            query.append("    FROM imx_documento d ");
            query.append("    WHERE d.NOMBRE_DOCUMENTO LIKE 'AVI -%-'+CONVERT( VARCHAR, re.nFolioReintegroAut)) ");
            query.append("INNER JOIN ");
            query.append("imx_documento d WITH (nolock) ");
            query.append("ON d.titulo_aplicacion=p.titulo_aplicacion ");
            query.append("AND d.id_gabinete=p.id_gabinete ");
            query.append("AND d.id_carpeta_padre=p.id_carpeta_padre ");
            query.append("AND d.id_documento=p.id_documento ");
            query.append("INNER JOIN ");
            query.append("imx_volumen v WITH (nolock) ");
            query.append("ON v.volumen=p.volumen ");
            query.append("AND d.NOMBRE_DOCUMENTO IN('PDF CLC', 'PDF CxP', 'Archivo Linea de Captura', 'Comprobante de pago', 'Reporte SICOP', 'Reporte SIAFF', 'Reporte') ");
            query.append("WHERE re.cDocumentoHaplicado='S' ");
            query.append("  AND d.TITULO_APLICACION='REINTEGRO' ");
            query.append("  AND d.NOMBRE_DOCUMENTO!='Archivo' ");
            query.append("  AND d.ID_CARPETA_PADRE!=0 ");
            query.append("  AND re.rechazoBanco IS NULL ");
            query.append("GROUP BY re.nFolioReintegroAut; ");
            pstm = conn.prepareStatement(query.toString());
            rs = pstm.executeQuery();
            while (rs.next()) {
                if (rs.getInt("cantArchivos") < 7)
                    mapFolios.put(rs.getString("nFolioReintegro"), "pendiente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return mapFolios;
    }

    public static Map<String, String> getFoliosDocPendientesReintegrosSPEI() {
        ResultSet rs = null;
        PreparedStatement pstm = null;
        Map<String, String> mapFolios = new HashMap<String, String>();
        Connection conn = null;
        try {
            StringBuffer query = new StringBuffer();
            conn = RefasBusinessLogic.getConnection(GestionInterface.ATT_CONEXION);
            query.append(" SELECT re.nFolioReintegroaut, COUNT(re.nFolioReintegroaut) AS cantArchivos ");
            query.append("FROM dbo.tReintegroAutEncabezado re WITH (nolock) ");
            query.append("INNER JOIN ");
            query.append("imx_pagina p WITH (nolock) ");
            query.append("ON p.TITULO_APLICACION='REINTEGRO' ");
            query.append("AND p.id_gabinete=( ");
            query.append("    SELECT ID_GABINETE ");
            query.append("    FROM imx_documento d ");
            query.append("    WHERE d.NOMBRE_DOCUMENTO LIKE 'AVI -%-'+CONVERT( VARCHAR, re.nFolioReintegroaut)) ");
            query.append("INNER JOIN ");
            query.append("imx_documento d WITH (nolock) ");
            query.append("ON d.titulo_aplicacion=p.titulo_aplicacion ");
            query.append("AND d.id_gabinete=p.id_gabinete ");
            query.append("AND d.id_carpeta_padre=p.id_carpeta_padre ");
            query.append("AND d.id_documento=p.id_documento ");
            query.append("INNER JOIN ");
            query.append("imx_volumen v WITH (nolock) ");
            query.append("ON v.volumen=p.volumen ");
            query.append("AND d.NOMBRE_DOCUMENTO IN('PDF CLC', 'PDF CxP', 'Archivo Linea de Captura', 'Comprobante de pago', 'Reporte SICOP', 'Reporte SIAFF', 'Reporte') ");
            query.append("WHERE re.cDocumentoHaplicado='S' ");
            query.append("  AND d.TITULO_APLICACION='REINTEGRO' ");
            query.append("  AND d.NOMBRE_DOCUMENTO!='Archivo' ");
            query.append("  AND d.ID_CARPETA_PADRE!=0 ");
            query.append("  AND re.rechazoBanco=1 ");
            query.append("GROUP BY re.nFolioReintegroaut; ");
            pstm = conn.prepareStatement(query.toString());
            rs = pstm.executeQuery();
            while (rs.next()) {
                if (rs.getInt("cantArchivos") < 6)
                    mapFolios.put(rs.getString("nFolioReintegro"), "pendiente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return mapFolios;
    }

    public static Map<String, String> getFoliosDocPendientesReintegrosMil() {
        ResultSet rs = null;
        PreparedStatement pstm = null;
        Map<String, String> mapFolios = new HashMap<String, String>();
        Connection conn = null;
        try {
            conn = RefasBusinessLogic.getConnection(GestionInterface.ATT_CONEXION);
            StringBuffer query = new StringBuffer();
            query.append(" SELECT re.nFolioReintegroMilaut, ");
            query.append("       COUNT(re.nFolioReintegroMilaut) AS cantArchivos ");
            query.append("FROM dbo.tReintegroAutEncabezadoMil re WITH (nolock) ");
            query.append("INNER JOIN ");
            query.append("imx_pagina p WITH (nolock) ");
            query.append("ON p.TITULO_APLICACION='REINTEGROMIL' ");
            query.append("AND p.id_gabinete=( ");
            query.append("    SELECT ID_GABINETE ");
            query.append("    FROM imx_documento d ");
            query.append("    WHERE d.NOMBRE_DOCUMENTO LIKE 'AVIN-%-'+CONVERT( VARCHAR, re.nFolioReintegroMilaut)) ");
            query.append("INNER JOIN ");
            query.append("imx_documento d WITH (nolock) ");
            query.append("ON d.titulo_aplicacion=p.titulo_aplicacion ");
            query.append("AND d.id_gabinete=p.id_gabinete ");
            query.append("AND d.id_carpeta_padre=p.id_carpeta_padre ");
            query.append("AND d.id_documento=p.id_documento ");
            query.append("INNER JOIN ");
            query.append("imx_volumen v WITH (nolock) ");
            query.append("ON v.volumen=p.volumen ");
            query.append("AND d.NOMBRE_DOCUMENTO IN('PDF CLC', 'PDF CxP', 'Archivo Linea de Captura', 'Comprobante de pago', 'Reporte SICOP', 'Reporte SIAFF', 'Reporte') ");
            query.append("WHERE re.cDocumentoHaplicado='S' ");
            query.append("  AND d.TITULO_APLICACION='REINTEGROMIL' ");
            query.append("  AND d.NOMBRE_DOCUMENTO!='Archivo' ");
            query.append("  AND d.ID_CARPETA_PADRE!=0 ");
            query.append("GROUP BY re.nFolioReintegroMilaut; ");
            pstm = conn.prepareStatement(query.toString());
            rs = pstm.executeQuery();
            while (rs.next()) {
                if (rs.getInt("cantArchivos") < 7)
                    mapFolios.put(rs.getString("nFolioReintegroMil"), "pendiente");
            }
        } catch (SQLException e) {
            Log.info("Error occurred", e);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                Log.info("Error occurred", e);
            }
        }
        return mapFolios;
    }
}
