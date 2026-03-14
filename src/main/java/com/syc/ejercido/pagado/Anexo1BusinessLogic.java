package com.syc.ejercido.pagado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.ejercido.pagado.core.Anexo1Manager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class Anexo1BusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(Anexo1BusinessLogic.class);

    private Anexo1Encabezado encabezado;

    private List<Anexo1Detalle> detalle;

    private String folioGenerator;

    public Anexo1BusinessLogic(String jniName) {
        super.init(jniName);
    }

    public Anexo1BusinessLogic(String jniName, String folioGenerator) {
        super.init(jniName);
        this.folioGenerator = folioGenerator;
    }

    public static Anexo1Encabezado instanceHeaderFromRequest(HttpServletRequest req) throws Exception {
        Anexo1Encabezado encabezado = new Anexo1Encabezado();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        encabezado.setnFolioAnexo(Integer.parseInt(req.getParameter("nFolioAnexo"), 10));
        encabezado.setfCaptura(formatter.parse(req.getParameter("fAplicacion")));
        encabezado.setfApliacion(formatter.parse(req.getParameter("fAplicacion")));
        encabezado.setcConcepto(req.getParameter("cConcepto"));
        encabezado.setcaNoContrarrecibo(req.getParameter("caNoContrarrecibo"));
        encabezado.setcTipoAnexo(req.getParameter("cTipoAnexo"));
        encabezado.setcIdContrato(req.getParameter("cIdContrato"));
        encabezado.setmImporte(Double.parseDouble(req.getParameter("importeTotal")));
        encabezado.setu_login(req.getParameter("uLogin"));
        encabezado.setcCentroContable(req.getParameter("cCentroContable"));
        encabezado.setaEjercicioFiscal(req.getParameter("cEjercicio"));
        encabezado.setcUnidadResponsable(req.getParameter("cUnidadResponsable"));
        encabezado.setcRamo("16");
        encabezado.setcDocumentoHaplicado(null);
        encabezado.setcTipoPoliza("PR");
        encabezado.setnFolioPoliza(0);
        encabezado.setnFolioPolizaCancelacion(0);
        encabezado.setfCancelacion(null);
        encabezado.setcUnidadResponsableContable("RHQ");
        encabezado.setfirmanteVoBo(req.getParameter("firmanteVoBo"));
        encabezado.setcPuestoVo(req.getParameter("cPuestoVo"));
        encabezado.setfirmanteAut(req.getParameter("firmanteAut"));
        encabezado.setcPuestoA(req.getParameter("cPuestoA"));
        return encabezado;
    }

    public static List<Anexo1Detalle> instanceDetailFromRequest(HttpServletRequest req) throws Exception {
        List<Anexo1Detalle> detalle = new ArrayList<Anexo1Detalle>();
        String[] eps = req.getParameterValues("epDetalle");
        String[] mImportes = req.getParameterValues("importeDetalle");
        String[] cMes = req.getParameterValues("mesDetalle");
        for (int i = 0; i < eps.length; i++) {
            Anexo1Detalle tmpDet = new Anexo1Detalle();
            tmpDet.setnFolioAnexo(Integer.parseInt(req.getParameter("nFolioAnexo"), 10));
            tmpDet.setnDocRenglon(i + 1);
            tmpDet.setEp(eps[i]);
            tmpDet.setcMes(cMes[i]);
            tmpDet.setcEvento(req.getParameter("cEvento"));
            tmpDet.setaEjercicioFiscal(req.getParameter("cEjercicio"));
            tmpDet.setcCentroContable(req.getParameter("cCentroContable"));
            tmpDet.setmImporte(Double.parseDouble(mImportes[i]));
            tmpDet.setmImporteNegativo(-Double.parseDouble(mImportes[i]));
            tmpDet.setOBGT("");
            detalle.add(tmpDet);
        }
        return detalle;
    }

    public void setEncabezado(Anexo1Encabezado encabezado) {
        this.encabezado = encabezado;
    }

    public Anexo1Encabezado getEncabezado() {
        return encabezado;
    }

    public List<Anexo1Detalle> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<Anexo1Detalle> detalle) {
        this.detalle = detalle;
    }

    public int insert() throws Exception {
        Connection conn = null;
        int insertados = 0;
        try {
            conn = getConnection();
            if (!Anexo1Manager.exists(conn, getEncabezado().getnFolioAnexo())) {
                insertados = Anexo1Manager.insertEncabezado(conn, getEncabezado());
                insertados += Anexo1Manager.insertDetalle(conn, getDetalle());
            } else {
                insertados = Anexo1Manager.updateEncabezado(conn, getEncabezado());
                insertados += Anexo1Manager.insertDetalle(conn, getDetalle());
            }
            conn.commit();
            return insertados;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("No se pudo realizar rollback. Causa: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public String getUE(String sCB) throws Exception {
        PreparedStatement pstmntH = null;
        Connection conn = null;
        ResultSet rs3 = null;
        conn = getConnection();
        String sRet = "";
        String Sql3 = " select strUnidadEjecutora from tUECuentasBancarias where strclabe = '" + sCB + "'";
        pstmntH = conn.prepareStatement(Sql3);
        rs3 = pstmntH.executeQuery();
        if (rs3.next()) {
            sRet = rs3.getString(1);
        }
        rs3.close();
        return sRet;
    }

    public ArrayList<String> buscaAnexo1Integrados(String listaFolios, String listaCuentaBancaria, String listaFechas, String ListaLeyendas, Usuario usuario, String pTimeStamp) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = Anexo1Manager.buscaAnexo1Integrados(conn, listaFolios, listaCuentaBancaria, listaFechas, ListaLeyendas, usuario, pTimeStamp, folioGenerator);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                log.error(e.getMessage(), e);
                try {
                    conn.rollback();
                } catch (Exception e3) {
                    log.error("Problemas haciendo rollback " + e3, e3);
                }
                throw new GestionException(e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrListaComp;
    }

    public ArrayList<String> ArmaDocumentoComprobatorio(String listaIds, String sTimeStamp, boolean bIntegra) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = Anexo1Manager.CreaDocumentacionComprobatoria(conn, listaIds, sTimeStamp, bIntegra);
            Anexo1Manager.updateHeaderAnexo1EnvioSICOP(conn, listaIds);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrListaComp;
    }

    public boolean ActualizaStatus(String listaFolios, String usuario) throws Exception {
        Connection conn = null;
        Boolean Actualizado = null;
        try {
            conn = getConnection();
            Anexo1Manager.UpdateStatus(conn, listaFolios, usuario);
            Actualizado = true;
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                Actualizado = false;
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return Actualizado;
    }

    public void recorreRenglones(Integer nFolio) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            Anexo1Manager.updateRenglones(conn, nFolio);
            Anexo1Manager.insertUpdateApartadoNuevo(conn, nFolio);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }

    public void insertUpdateApartado(Integer nFolio, String accion) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            Anexo1Manager.insertUpdateApartado(conn, nFolio, accion);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }
}
