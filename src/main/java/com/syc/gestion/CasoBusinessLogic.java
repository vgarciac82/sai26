package com.syc.gestion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.jenkov.prizetags.tree.impl.Tree;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.dsmngr.DataSourceManager;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.CarpetaManager;
import com.syc.fortimax.core.Descripcion;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.DocumentoVersionManager;
import com.syc.fortimax.core.Fortimax;
import com.syc.fortimax.core.OrgCarpeta;
import com.syc.fortimax.core.OrgCarpetaManager;
import com.syc.fortimax.core.Pagina;
import com.syc.fortimax.core.PaginaManager;
import com.syc.fortimax.core.TipoDocumento;
import com.syc.fortimax.core.TipoDocumentoManager;
import com.syc.fortimax.core.Volumen;
import com.syc.fortimax.core.VolumenManager;
import com.syc.fortimax.exceptions.FortimaxException;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.BitacoraManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDato;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoHerramientas;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.gestion.core.FortimaxFile;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Operacion;
import com.syc.gestion.core.OperacionManager;
import com.syc.gestion.core.TipoCasoManager;
import com.syc.gestion.core.TreeManager;
import com.syc.gestion.core.URLDocumento;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.sai.bitacora.BitacoraOperacionDoctosBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.procesosAutomaticos.UploadConciliacionBusinessLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CasoBusinessLogic extends DataSourceManager {

    public static final String CASO_END = "TERMINAR";

    private static Logger log = LoggerFactory.getLogger(CasoBusinessLogic.class);

    public static final String OPER_CONTINUE = "CONTINUAR";

    private Map tciMap;

    private UploadConciliacionBusinessLogic ucbl;

    public CasoBusinessLogic() {
    }

    public CasoBusinessLogic(String jniName) {
        super.init(jniName);
        try {
            ucbl = new UploadConciliacionBusinessLogic(jniName, null);
        } catch (Exception e) {
            log.error("Object: {}", "No se logro crear instancia de conciliacion bancarias. Se intenta continuar" + e);
        }
    }

    public Caso actualizaCasoDato(Connection conn, Caso c, Map data) throws GestionException {
        try {
            CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), data);
            AplicacionManager.updateExpediente(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), data);
            return getCaso(conn, c.getIdCaso(), c.getCasoOperacion(0).getIdCasoOper());
        } catch (Exception e) {
            throw new GestionException(e.toString(), e);
        }
    }

    public Caso actualizaCasoDato(Caso c, Map data) throws GestionException {
        Connection conn = null;
        try {
            conn = getConnection();
            actualizaCasoDato(conn, c, data);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return getCaso(c.getIdCaso(), c.getCasoOperacion(0).getIdCasoOper());
    }

    public void actualizaCoResponsable(int id_caso, int id_tc, String responsable) throws GestionException {
        Connection conn = null;
        try {
            conn = getConnection();
            CasoDatoManager.updateCoResponsable(conn, id_caso, id_tc, responsable);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public void actualizaDocumentoGestion(Fortimax fimx) throws GestionException {
        Connection conn = null;
        try {
            conn = getConnection();
            DocumentoManager.upateDocumento(conn, fimx.getTituloAplicacion(), fimx.getIdGabinete(), fimx.getIdCarpeta(), fimx.getIdDocumento(), "IMAX_FILE");
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("Actualizando Documento a tipo Fortimax", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public void actualizaResponsable(int id_caso, int id_tc) throws GestionException {
        Connection conn = null;
        try {
            conn = getConnection();
            CasoDatoManager.updateResponsable(conn, id_caso, id_tc);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public synchronized Caso avanzaCaso(Caso c, String u_login, String observ, String[] resp, String[] oper, Map data, String pathPrefix) throws GestionException {
        Caso rco = null;
        Connection conn = null;
        boolean delete = true;
        if (resp.length != oper.length) {
            log.error("Object: {}", "Las dimensiones no coinciden rep[" + resp.length + "] vs oper[" + oper.length + "]");
            throw new GestionException("Las dimensiones no coinciden rep[" + resp.length + "] vs oper[" + oper.length + "]");
        }
        try {
            int[] idCasoOperSgte = new int[resp.length];
            conn = getConnection();
            rco = avanzaCaso(conn, c, u_login, observ, resp, oper, data, pathPrefix);
            conn.commit();
        } catch (IOException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("No se logro avanzar el caso (" + c.getIdCaso() + ", " + c.getCasoOperacion(0).getIdCasoOper() + ")", exc);
            throw new GestionException("No se logro avanzar el caso (" + c.getIdCaso() + ", " + c.getCasoOperacion(0).getIdCasoOper() + ")", exc);
        } catch (SQLException exc) {
            log.error(exc.getMessage(), exc);
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("No se logro avanzar el caso (" + c.getIdCaso() + ", " + c.getCasoOperacion(0).getIdCasoOper() + ")", exc);
            throw new GestionException("No se logro avanzar el caso (" + c.getIdCaso() + ", " + c.getCasoOperacion(0).getIdCasoOper() + ")", exc);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("No se logro avanzar el caso (" + c.getIdCaso() + ", " + c.getCasoOperacion(0).getIdCasoOper() + ")", exc);
            throw new GestionException("No se logro avanzar el caso (" + c.getIdCaso() + ", " + c.getCasoOperacion(0).getIdCasoOper() + ")", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return rco;
    }

    public synchronized Caso avanzaCaso(Connection conn, Caso c, String u_login, String observ, String[] resp, String[] oper, Map<String, String> data, String pathPrefix) throws GestionException, IOException, SQLException {
        Caso rco = null;
        boolean delete = true;
        if (resp.length != oper.length) {
            log.error("Object: {}", "Las dimensiones no coinciden rep[" + resp.length + "] vs oper[" + oper.length + "]");
            throw new GestionException("Las dimensiones no coinciden rep[" + resp.length + "] vs oper[" + oper.length + "]");
        }
        int[] idCasoOperSgte = new int[resp.length];
        CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), data);
        for (int i = 0; i < resp.length; i++) {
            if (OPER_CONTINUE.equalsIgnoreCase(oper[i].trim())) {
                c.setStatus(100);
                CasoManager.update(conn, c);
                CasoOperacionManager.updateObservacion(conn, c, observ);
                delete = false;
                continue;
            } else if (CASO_END.equalsIgnoreCase(oper[i].trim())) {
                delete = false;
                for (int j = i + 1; j < idCasoOperSgte.length; j++) idCasoOperSgte[j] = -1;
                CasoManager.terminaCaso(conn, c, resp, oper);
                TipoCasoInterface tci = null;
                if (c.getTipoCaso().tieneInterface()) {
                    tci = instanceTipoCasoInterface(c.getTipoCaso().getInterface());
                    tci.onTerminaCaso(conn, u_login, c, "", resp, oper, data);
                }
                break;
            }
            Operacion o = new Operacion();
            o.setIdTC(c.getIdTC());
            o.setNombre(oper[i].trim());
            o = OperacionManager.select(conn, o);
            if (o == null) {
                log.error("Object: {}", "No se localizo la Operacion \"" + oper[i] + "\"");
                throw new GestionException("No se localizo la Operación \"" + oper[i] + "\"");
            }
            CasoOperacion co = CasoOperacionManager.nuevoCasoOperacion(conn, resp[i].trim(), observ, c, o);
            CasoOperacionManager.insert(conn, co);
            idCasoOperSgte[i] = co.getIdCasoOper();
            TipoCasoInterface tci = null;
            if (c.getTipoCaso().tieneInterface()) {
                tci = instanceTipoCasoInterface(c.getTipoCaso().getInterface());
                tci.onAvanzaCaso(conn, u_login, c, co.getIdOperacion());
            }
        }
        if (delete) {
            BitacoraManager.registraCasoOperacion(conn, u_login, c, idCasoOperSgte, resp, oper);
            // Se comenta el delete ya que para evitar duplicados en inbox
            // se cambio el inser por un update
            CasoOperacionManager.delete(conn, c.getIdCaso(), c.getCasoOperacion(0).getIdCasoOper());
        }
        if ((c.getStatus() & Caso.MSG_SENDED) == Caso.MSG_SENDED)
            c.setStatus(c.getStatus() ^ Caso.MSG_SENDED);
        c.setStatus(c.getStatus() ^ Caso.EXECUTED);
        CasoManager.update(conn, c);
        return rco;
    }

    public String borraDocumento(String nodeId) throws GestionException {
        String doc_name = null;
        Connection conn = null;
        try {
            conn = getConnection();
            Fortimax fimx = new Fortimax(nodeId);
            Carpeta c = CarpetaManager.getCarpeta(conn, fimx.getTituloAplicacion(), fimx.getIdGabinete(), fimx.getIdCarpeta());
            if ("CFDI".equalsIgnoreCase(c.getNombreCarpeta()))
                throw new GestionException("No se pueden modificar facturas. Solicite apoyo con el administrador.");
            FortimaxFile[] files = PaginaManager.getPaginasDeDocumento(conn, fimx.getTituloAplicacion(), fimx.getIdGabinete(), fimx.getIdCarpeta(), fimx.getIdDocumento());
            for (int i = 0; i < files.length; i++) {
                if (!files[i].getFile().delete()) {
                    log.warn("Object: {}", "No se logro borrar archivo \"" + files[i].getFisicalName() + "\"");
                    files[i].getFile().deleteOnExit();
                }
            }
            Documento d = DocumentoManager.selectDocumento(conn, fimx.getTituloAplicacion(), fimx.getIdGabinete(), fimx.getIdCarpeta(), fimx.getIdDocumento());
            doc_name = d.getNombreDocumento();
            PaginaManager.delete(conn, fimx.getTituloAplicacion(), fimx.getIdGabinete(), fimx.getIdCarpeta(), fimx.getIdDocumento());
            DocumentoManager.delete(conn, fimx.getTituloAplicacion(), fimx.getIdGabinete(), fimx.getIdCarpeta(), fimx.getIdDocumento());
            conn.commit();
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("Borrando pagina", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return doc_name;
    }

    public void deleteDocument(Connection conn, String nodeId) throws Exception {
        Fortimax fimx = new Fortimax(nodeId);
        Carpeta folder = CarpetaManager.getCarpeta(conn, fimx.getTituloAplicacion(), fimx.getIdGabinete(), fimx.getIdCarpeta());
        if ("CFDI".equalsIgnoreCase(folder.getNombreCarpeta()))
            throw new GestionException("No se pueden modificar facturas. Solicite apoyo con el administrador.");
        FortimaxFile[] files = PaginaManager.getPaginasDeDocumento(conn, fimx.getTituloAplicacion(), fimx.getIdGabinete(), fimx.getIdCarpeta(), fimx.getIdDocumento());
        for (int i = 0; i < files.length; i++) {
            if (!files[i].getFile().delete()) {
                log.warn("Object: {}", "No se logro borrar archivo \"" + files[i].getFisicalName() + "\"");
                files[i].getFile().deleteOnExit();
            }
        }
        Documento d = DocumentoManager.selectDocumento(conn, fimx.getTituloAplicacion(), fimx.getIdGabinete(), fimx.getIdCarpeta(), fimx.getIdDocumento());
        PaginaManager.delete(conn, fimx.getTituloAplicacion(), fimx.getIdGabinete(), fimx.getIdCarpeta(), fimx.getIdDocumento());
        DocumentoManager.delete(conn, fimx.getTituloAplicacion(), fimx.getIdGabinete(), fimx.getIdCarpeta(), fimx.getIdDocumento());
    }

    public int borraPaginaDocumento(String nodeId, int pagina) throws GestionException {
        Connection conn = null;
        FortimaxFile[] ff = new FortimaxFile[0];
        try {
            conn = getConnection();
            Fortimax f = new Fortimax(nodeId);
            ff = PaginaManager.getPaginasDeDocumento(conn, f.getTituloAplicacion(), f.getIdGabinete(), f.getIdCarpeta(), f.getIdDocumento());
            Pagina p = PaginaManager.selectPagina(conn, f.getTituloAplicacion(), f.getIdGabinete(), f.getIdCarpeta(), f.getIdDocumento(), ff[pagina].getFisicalName());
            PaginaManager.deletePagina(conn, p);
            if (!ff[pagina].getFile().delete()) {
                ff[pagina].getFile().deleteOnExit();
                log.warn("Object: {}", "No se logro borrar pagina \"" + ff[pagina].getFile().getAbsolutePath() + "\"");
            }
            conn.commit();
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("Borrando pagina", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return ff.length;
    }

    /**
     * Busca el idDocumento de acuerdo al nombre del documento para hacerlo
     * dinamico sin importar que idDocumento se la halla asignado
     *
     * @param titulo_aplicacion
     *            Gaveta
     * @param id_gabinete
     *            Gabinete
     * @param id_carpeta_padre
     *            Carpeta Padre
     * @param doc_nombre
     *            Nombre del documento
     * @return el id del documento en el expediente.
     * @throws SQLException
     */
    public int buscaIdDocumento(String titulo_aplicacion, int id_gabinete, int id_carpeta_padre, String doc_nombre) throws Exception {
        int idDocumento = 0;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = getConnection();
            pstmnt = conn.prepareStatement("SELECT ID_DOCUMENTO FROM imx_documento WITH (NOLOCK) WHERE titulo_aplicacion = ?  AND id_gabinete = ? AND ID_CARPETA_PADRE = ? AND nombre_documento = ? ");
            pstmnt.setString(1, titulo_aplicacion);
            pstmnt.setInt(2, id_gabinete);
            pstmnt.setInt(3, id_carpeta_padre);
            pstmnt.setString(4, doc_nombre);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                idDocumento = rs.getInt(1);
            }
            return idDocumento;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstmnt, false);
            CloseObject.closeObject(conn);
        }
    }

    public Caso consultaCaso(int id_caso, int id_caso_oper) throws GestionException {
        Caso rc = null;
        Connection conn = null;
        try {
            conn = getConnection();
            Caso srchCase = new Caso();
            srchCase.setIdCaso(id_caso);
            rc = CasoManager.select(conn, srchCase, id_caso_oper);
        } catch (SQLException exc) {
            log.error("No se logro consultar el caso (" + id_caso + ", " + id_caso_oper + ")", exc);
            throw new GestionException("No se logro consultar el caso (" + id_caso + ", " + id_caso_oper + ")", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return rc;
    }

    public Caso consultaCasoContDiv(String cIdcontrato) throws Exception {
        Caso rc = null;
        Connection conn = null;
        int id_caso = -1;
        try {
            conn = getConnection();
            id_caso = CasoManager.obtieneIdCasoContDiv(conn, cIdcontrato);
            Caso srchCase = new Caso();
            srchCase.setIdCaso(id_caso);
            rc = CasoManager.select(conn, srchCase);
        } catch (SQLException exc) {
            log.error("No se logro consultar el caso (" + id_caso + ")", exc);
            throw new GestionException("No se logro consultar el caso (" + id_caso + ")", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return rc;
    }

    public Caso[] consultaCasoVencido(int id_oper, String co_responsable, Date co_fecha_ini) throws GestionException {
        Caso[] rc = null;
        Connection conn = null;
        try {
            conn = getConnection();
            rc = CasoManager.select(conn, id_oper, co_responsable, co_fecha_ini);
        } catch (SQLException exc) {
            log.error("No se logro consultar el caso (" + id_oper + ", " + co_responsable + ", " + co_fecha_ini + ")", exc);
            throw new GestionException("No se logro consultar el caso (" + id_oper + ", " + co_responsable + ", " + co_fecha_ini + ")", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return rc;
    }

    public synchronized void copiaPaginaADocumento(Fortimax f, int index, String titulo_aplicacion, int idDocto) throws GestionException {
        Connection conn = null;
        try {
            conn = getConnection();
            FortimaxFile[] ff = PaginaManager.getPaginasDeDocumento(conn, f.getTituloAplicacion(), f.getIdGabinete(), f.getIdCarpeta(), f.getIdDocumento());
            Volumen vol = VolumenManager.getVolumen(conn);
            TipoDocumento td = TipoDocumentoManager.buscaTipoDocumento(conn, f.getTituloAplicacion(), idDocto);
            Documento d = DocumentoManager.buscaDocumento(conn, f.getTituloAplicacion(), f.getIdGabinete(), td.getNombreTipoDocto());
            if (!d.getNombreTipoDocto().equals("IMAX_FILE"))
                DocumentoManager.upateDocumento(conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), d.getIdDocumento(), "IMAX_FILE");
            // Se recupera para tener los cambios del update
            d = DocumentoManager.selectDocumento(conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), d.getIdDocumento());
            File file = ff[index].getFile();
            DocumentoManager.insertPaginaDocumento(conn, vol, d, "A", file.length());
            // Se recupera para actualizar paginas
            d = DocumentoManager.selectDocumento(conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), d.getIdDocumento());
            String filename = d.getFullPathFilesNames()[d.getFullPathFilesNames().length - 1];
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            DataOutputStream out = new DataOutputStream(new FileOutputStream(filename));
            int fileLength = 0, length = 0;
            byte[] buffer = new byte[4 * 1024];
            while ((in != null) && ((length = in.read(buffer)) != -1)) {
                out.write(buffer, 0, length);
                fileLength += length;
            }
            in.close();
            out.close();
            conn.commit();
        } catch (IOException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("Actualizando Documento a tipo Fortimax", exc);
            throw new GestionException(exc);
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("Actualizando Documento a tipo Fortimax", exc);
            throw new GestionException(exc);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("Actualizando Documento a tipo Fortimax", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public void creaCarpeta(Usuario u, Fortimax fimx, String nombre, String desc) throws GestionException {
        Connection conn = null;
        try {
            conn = getConnection();
            Carpeta c = new Carpeta();
            c.setTituloAplicacion(fimx.getTituloAplicacion());
            c.setIdGabinete(fimx.getIdGabinete());
            c.setIdCarpeta(CarpetaManager.getNextIdCarpeta(conn, fimx.getTituloAplicacion(), fimx.getIdGabinete()));
            c.setNombreCarpeta(nombre);
            c.setNombreUsuario(u.getLogin());
            c.setBanderaRaiz("N");
            c.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
            c.setFechaModificacion(new Timestamp(System.currentTimeMillis()));
            c.setNumeroAccesos(0);
            c.setNumeroCarpetas(0);
            c.setNumeroDocumentos(0);
            c.setDescripcion(desc);
            c.setPassword("-1");
            CarpetaManager.insert(conn, c);
            OrgCarpeta oc = new OrgCarpeta();
            oc.setTituloAplicacion(c.getTituloAplicacion());
            oc.setIdGabinete(c.getIdGabinete());
            oc.setIdCarpetaHija(c.getIdCarpeta());
            oc.setIdCarpetaPadre(fimx.getIdCarpeta());
            oc.setNombreHija(nombre);
            OrgCarpetaManager.insert(conn, oc);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("No se logro crear la carpeta (" + nombre + ")", exc);
            throw new GestionException("No se logro crear la carpeta (" + nombre + ")", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public void creaDocumento(Usuario u, Fortimax fimx, String nombre, String desc, boolean isImgDoc) throws GestionException {
        Connection conn = null;
        try {
            conn = getConnection();
            Documento d = new Documento();
            d.setTituloAplicacion(fimx.getTituloAplicacion());
            d.setIdGabinete(fimx.getIdGabinete());
            d.setIdCarpetaPadre(fimx.getIdCarpeta());
            d.setIdDocumento(-1);
            d.setNombreDocumento(nombre);
            d.setNombreUsuario(u.getLogin());
            d.setDescripcion(desc);
            d.setNombreTipoDocto((isImgDoc ? "IMAX_FILE" : "EXTERNO"));
            DocumentoManager.insertDocumento(conn, d);
            conn.commit();
        } catch (SQLException | FortimaxException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("No se logro crear la carpeta (" + nombre + ")", exc);
            throw new GestionException("No se logro crear la carpeta (" + nombre + ")", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public void creaDocumento(Usuario u, Fortimax fimx, String nombre, String desc, boolean isImgDoc, Timestamp fh_vigencia) throws GestionException {
        Connection conn = null;
        try {
            conn = getConnection();
            Documento d = new Documento();
            d.setTituloAplicacion(fimx.getTituloAplicacion());
            d.setIdGabinete(fimx.getIdGabinete());
            d.setIdCarpetaPadre(fimx.getIdCarpeta());
            d.setIdDocumento(-1);
            d.setNombreDocumento(nombre);
            d.setNombreUsuario(u.getLogin());
            d.setDescripcion(desc);
            d.setNombreTipoDocto((isImgDoc ? "IMAX_FILE" : "EXTERNO"));
            d.setFh_vigencia(fh_vigencia);
            DocumentoManager.insertDocumento(conn, d);
            conn.commit();
        } catch (SQLException | FortimaxException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("No se logro crear la carpeta (" + nombre + ")", exc);
            throw new GestionException("No se logro crear la carpeta (" + nombre + ")", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public synchronized int creaExpediente(String u_login, Caso c) throws GestionException {
        int id_gabinete = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            id_gabinete = creaExpediente(conn, u_login, c);
            conn.commit();
            return id_gabinete;
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("No se logro crear el Expediente del caso (" + c.getIdCaso() + ")", exc);
            throw new GestionException("No se logró crear el Expediente del caso (" + c.getIdCaso() + ")", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public synchronized int creaExpediente(Connection conn, String login, Caso c) throws GestionException, SQLException {
        int id_gabinete = -1;
        Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
        id_gabinete = AplicacionManager.createExpediente(conn, login, c, app);
        if (id_gabinete < 0) {
            log.error("Identificador de Gabiente inválido (< 0)");
            throw new SQLException("Identificador de Gabiente inválido (< 0)");
        }
        c.setIdGabinete(id_gabinete);
        CasoManager.update(conn, c);
        TipoCasoInterface tci = null;
        if (c.getTipoCaso().tieneInterface()) {
            tci = instanceTipoCasoInterface(c.getTipoCaso().getInterface());
            tci.onCreateExpediente(conn, login, c, app);
        }
        return id_gabinete;
    }

    public Caso ejecutaCaso(int id_caso, int id_caso_oper, String u_nombre) throws GestionException {
        Caso rc = null;
        Connection conn = null;
        try {
            conn = getConnection();
            Caso srchCase = new Caso();
            srchCase.setIdCaso(id_caso);
            rc = CasoManager.select(conn, srchCase, id_caso_oper);
            CasoOperacion co = rc.getCasoOperacion(0);
            co.setResponsable(u_nombre);
            co.setStatus(co.getStatus() | CasoOperacion.EXECUTED);
            CasoOperacionManager.update(conn, co);
            rc.setCasoOperacion(new Vector());
            rc.setCasoOperacion(co);
            rc.setStatus(rc.getStatus() | Caso.EXECUTED);
            CasoManager.update(conn, rc);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("No se logro ejecutar el caso (" + id_caso + ", " + id_caso_oper + ")", exc);
            throw new GestionException("No se logro ejecutar el caso (" + id_caso + ", " + id_caso_oper + ")", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return rc;
    }

    public Caso findByFolioLike(String tituloAplicacion, String folio) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return CasoManager.findByFolioLike(conn, tituloAplicacion, folio);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public Map getAllTipoCaso() throws GestionException {
        Connection conn = null;
        Map rm = new LinkedHashMap();
        try {
            conn = getConnection();
            rm = TipoCasoManager.selectAllTipoCasos(conn);
        } catch (SQLException exc) {
            log.error("Recuperando todos los Tipos de Caso", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Error cerrando conexi\u00F3n a base de datos", exc);
            }
            conn = null;
        }
        return rm;
    }

    public Map getAllTipoCaso(String u_login) throws GestionException {
        Connection conn = null;
        Map rm = new LinkedHashMap();
        try {
            conn = getConnection();
            rm = TipoCasoManager.selectAllTipoCasos(conn, u_login);
        } catch (SQLException exc) {
            log.error("Recuperando Tipos de Caso por Usuario", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Error cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return rm;
    }

    public ITree getArbolCaso(Caso c) throws GestionException {
        Connection conn = null;
        ITree tree = new Tree();
        try {
            conn = getConnection();
            tree = TreeManager.getTree(conn, c);
        } catch (SQLException exc) {
            log.error("Creando el Arbol del Caso", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Error cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return tree;
    }

    public FortimaxFile[] getArchivosDeDocumento(String titApp, int idGab, int idCarp, int idDoc) throws GestionException {
        FortimaxFile[] files = new FortimaxFile[0];
        Connection conn = null;
        try {
            conn = getConnection();
            files = PaginaManager.getPaginasDeDocumento(conn, titApp, idGab, idCarp, idDoc);
        } catch (Exception exc) {
            log.error("Creando el Arbol del Caso", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Error cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return files;
    }

    public File[] getArchivosDocumento(String titApp, int idGab, int idCarp, int idDoc) throws GestionException {
        File[] files = new File[0];
        Connection conn = null;
        try {
            conn = getConnection();
            String[] filenames = PaginaManager.getPaginasDocumento(conn, titApp, idGab, idCarp, idDoc);
            files = new File[filenames.length];
            for (int i = 0; i < filenames.length; i++) files[i] = new File(filenames[i]);
        } catch (SQLException exc) {
            log.error("Creando el Arbol del Caso", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Error cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return files;
    }

    public Caso getCaso(int id_caso) throws GestionException {
        Connection conn = null;
        Caso rc = null;
        try {
            conn = getConnection();
            Caso sc = new Caso();
            sc.setIdCaso(id_caso);
            rc = CasoManager.select(conn, sc);
        } catch (SQLException exc) {
            log.error("Obteniendo Caso(" + id_caso + ")", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return rc;
    }

    public Caso getCaso(int id_caso, int id_caso_operacion) throws GestionException {
        Connection conn = null;
        Caso rc = null;
        try {
            conn = getConnection();
            rc = getCaso(conn, id_caso, id_caso_operacion);
        } catch (SQLException exc) {
            log.error("Obteniendo Caso(" + id_caso + "), Caso Operacion(" + id_caso_operacion + ")", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return rc;
    }

    public Caso getCaso(Connection conn, int id_caso, int id_caso_operacion) throws GestionException {
        Caso rc = null;
        try {
            Caso sc = new Caso();
            sc.setIdCaso(id_caso);
            rc = CasoManager.select(conn, sc, id_caso_operacion);
            return rc;
        } catch (SQLException exc) {
            log.error("Obteniendo Caso(" + id_caso + "), Caso Operacion(" + id_caso_operacion + ")", exc);
            throw new GestionException(exc);
        }
    }

    /**
     * Busca un caso activo mediante su folio.
     *
     * @param cFolio
     *            Folio del caso
     * @return Caso que corresponde al folio buscado, null en caso de no
     *         existir.
     * @throws GestionException
     */
    public Caso getCaso(String cFolio) throws GestionException {
        Connection conn = null;
        Caso rc = null;
        try {
            conn = getConnection();
            Caso sc = new Caso();
            sc.setFolio(cFolio);
            rc = CasoManager.select(conn, sc);
            return rc;
        } catch (Exception exc) {
            log.error("Obteniendo Caso(" + cFolio + ")", exc);
            throw new GestionException(exc);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    /**
     * Obtiene el caso, desde su gaveta asociada y el id gabinete.
     *
     * @param tituloAplicacion
     *            Gaveta Asociada al caso.
     * @param idGabinete
     *            ID Gabinete del expedietne creado.
     */
    public Caso getCaso(String tituloAplicacion, int idGabinete) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return CasoManager.select(conn, tituloAplicacion, idGabinete);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public CasoHerramientas getCasoHerramientas(int id_tc, int id_oper) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return TipoCasoManager.getCasoHerramientas(conn, id_tc, id_oper);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    // Ethiel, obtiene todos los documentos de una carpeta
    public ArrayList<Documento> getDocumentosDeCarpeta(String titApp, int idGab, int idCarp) throws GestionException {
        ArrayList<Documento> docs = new ArrayList<Documento>();
        Connection conn = null;
        try {
            conn = getConnection();
            docs = DocumentoManager.getDocumentosDeCarpeta(conn, titApp, idGab, idCarp);
        } catch (SQLException exc) {
            log.error("Leyendo documentos de carpeta", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Error cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return docs;
    }

    public String getFilenamePath(Caso c) throws GestionException {
        Connection conn = null;
        String filename = null;
        try {
            conn = getConnection();
            Documento d = DocumentoManager.getDocumento(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 0, c.getFolio());
            if (d != null)
                filename = PaginaManager.getFilenamePath(conn, d.getTituloAplicacion(), d.getIdGabinete(), // .getIdCarpetaPadre(), 1);
                d.getIdCarpetaPadre(), d.getIdDocumento());
        } catch (SQLException | FortimaxException exc) {
            log.error("Obteniendo el Path del Archivo", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return filename;
    }

    public Map getGavetasFortimax() throws GestionException {
        Connection conn = null;
        Map rm = new LinkedHashMap();
        try {
            conn = getConnection();
            rm = AplicacionManager.select(conn);
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("Recuperando gavetas Fortimax", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return rm;
    }

    public URLDocumento getIdNodeDocumento(Caso c, String doc_nombre) throws GestionException {
        Connection conn = null;
        URLDocumento urlDoc = null;
        try {
            conn = getConnection();
            Documento d = DocumentoManager.buscaDocumento(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), doc_nombre);
            if (d == null)
                throw new GestionException("No se localizo el documento '" + doc_nombre + "'");
            urlDoc = new URLDocumento(d.getTituloAplicacion() + "_G" + d.getIdGabinete() + "C" + d.getIdCarpetaPadre() + "D" + d.getIdDocumento(), "IMAX_FILE".equals(d.getNombreTipoDocto()));
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("Recuperando documento por nombre '" + doc_nombre + "' del caso " + c.getIdCaso(), exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return urlDoc;
    }

    public synchronized Caso IniciaCaso(String username, String ur, int id_tc) throws GestionException {
        Caso rc = null;
        Connection conn = null;
        try {
            conn = getConnection();
            rc = CasoManager.nuevoCaso(conn, username, ur, id_tc);
            TipoCasoInterface tci = null;
            if (rc.getTipoCaso().tieneInterface()) {
                tci = instanceTipoCasoInterface(rc.getTipoCaso().getInterface());
                tci.onIniciaCaso(conn, username, rc);
            }
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("No se logro crear caso (" + id_tc + ")", exc);
            throw new GestionException("No se logro crear caso (" + id_tc + ")", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return rc;
    }

    // public synchronized Caso IniciaCaso(String u_nombre, int id_tc,
    // FolioGeneratorInterface folioGenerator)
    public synchronized Caso IniciaCaso(Usuario u, int id_tc, FolioGeneratorInterface folioGenerator) throws GestionException {
        Caso rc = null;
        Connection conn = null;
        try {
            conn = getConnection();
            rc = CasoManager.nuevoCaso(conn, u, id_tc, folioGenerator);
            TipoCasoInterface tci = null;
            if (rc.getTipoCaso().tieneInterface()) {
                tci = instanceTipoCasoInterface(rc.getTipoCaso().getInterface());
                tci.onIniciaCaso(conn, u.getLogin(), rc);
            }
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("No se logro crear caso (" + id_tc + ")", exc);
            throw new GestionException("No se logro crear caso (" + id_tc + ")", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return rc;
    }

    public synchronized Caso IniciaCaso(Connection conn, Usuario u, int id_tc, FolioGeneratorInterface folioGenerator) throws GestionException {
        Caso rc = null;
        try {
            conn = getConnection();
            rc = CasoManager.nuevoCaso(conn, u, id_tc, folioGenerator);
            TipoCasoInterface tci = null;
            if (rc.getTipoCaso().tieneInterface()) {
                tci = instanceTipoCasoInterface(rc.getTipoCaso().getInterface());
                tci.onIniciaCaso(conn, u.getLogin(), rc);
            }
        } catch (SQLException exc) {
            log.error("No se logro crear caso (" + id_tc + ")", exc);
            throw new GestionException("No se logro crear caso (" + id_tc + ")", exc);
        }
        return rc;
    }

    private TipoCasoInterface instanceTipoCasoInterface(String name) throws GestionException {
        TipoCasoInterface tci = null;
        if (tciMap == null)
            tciMap = new Hashtable();
        if (name == null)
            throw new GestionException("'name' no debe ser nulo");
        tci = (TipoCasoInterface) tciMap.get(name);
        if (tci != null)
            return tci;
        try {
            ClassLoader cl = getClass().getClassLoader();
            Class clase = cl.loadClass(name);
            tci = (TipoCasoInterface) clase.newInstance();
            tciMap.put(name, tci);
        } catch (ClassNotFoundException exc) {
            log.error("TipoCasoInterface " + name, exc);
            throw new GestionException(exc);
        } catch (InstantiationException exc) {
            log.error("TipoCasoInterface " + name, exc);
            throw new GestionException(exc);
        } catch (IllegalAccessException exc) {
            log.error("TipoCasoInterface " + name, exc);
            throw new GestionException(exc);
        }
        return tci;
    }

    public synchronized void liberaCaso(Caso c, String u_login, String observ, String[] resp, String[] oper, Map data, String pathPrefix) throws GestionException {
        Caso rco = null;
        Connection conn = null;
        if (resp.length != oper.length) {
            log.error("Object: {}", "Las dimensiones no coinciden rep[" + resp.length + "] vs oper[" + oper.length + "]");
            throw new GestionException("Las dimensiones no coinciden rep[" + resp.length + "] vs oper[" + oper.length + "]");
        }
        try {
            int[] idCasoOperSgte = new int[resp.length];
            conn = getConnection();
            CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), data);
            for (int i = 0; i < resp.length; i++) {
                Operacion o = new Operacion();
                o.setIdTC(c.getIdTC());
                o.setNombre(oper[i].trim());
                o = OperacionManager.select(conn, o);
                if (o == null) {
                    log.error("Object: {}", "No se localizo la Operacion \"" + oper[i] + "\"");
                    throw new GestionException("No se localizo la Operación \"" + oper[i] + "\"");
                }
                // se manda el caso operacion actual y el responsable nuevo que
                // es el grupo;
                CasoOperacionManager.update(conn, c.getCasoOperacion(0), resp[i].trim());
                idCasoOperSgte[i] = c.getCasoOperacion(0).getIdCasoOper();
            }
            BitacoraManager.registraCasoOperacion(conn, u_login, c, idCasoOperSgte, resp, oper);
            CasoManager.update(conn, c);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("No se logro avanzar el caso (" + c.getIdCaso() + ", " + c.getCasoOperacion(0).getIdCasoOper() + ")", exc);
            throw new GestionException("No se logro avanzar el caso (" + c.getIdCaso() + ", " + c.getCasoOperacion(0).getIdCasoOper() + ")", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public synchronized void recibeDocumentoGestion(Caso c, DataInputStream in) throws GestionException {
        recibeDocumentoGestion(c, 0, c.getFolio(), "xml", in, false);
    }

    public synchronized void recibeDocumentoGestion(Caso c, int id_carpeta, String nombreDocumento, String ext, DataInputStream in, boolean addPage) throws GestionException {
        Connection conn = null;
        FileOutputStream fos = null;
        try {
            conn = getConnection();
            Volumen vol = VolumenManager.getVolumen(conn);
            Documento d = DocumentoManager.getDocumento(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), id_carpeta, nombreDocumento);
            if (d == null) {
                d = new Documento();
                d.setTituloAplicacion(c.getTipoCaso().getGavetaAsociada());
                d.setIdGabinete(c.getIdGabinete());
                d.setIdCarpetaPadre(CarpetaManager.getIdCarpetaPadre(conn, d.getTituloAplicacion(), d.getIdGabinete()));
                // FIXME Se debe identificar el tipo de documento
                if ("imx".equals(ext))
                    d.setNombreTipoDocto("IMAX_FILE");
                else
                    d.setNombreTipoDocto("EXTERNO");
                d.setNombreDocumento(nombreDocumento);
                if (c.getCasoOperacion(0) != null) {
                    d.setNombreUsuario(c.getCasoOperacion(0).getResponsable());
                }
                d.setExtension(ext);
                DocumentoManager.insertDocumento(conn, d);
                DocumentoManager.insertPaginaDocumento(conn, vol, d, "A", 0);
                // Lee nuevamente el documento para actualizar las paginas
                d = DocumentoManager.getDocumento(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), id_carpeta, nombreDocumento);
            }
            String filename = PaginaManager.getFilenamePath(conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), d.getIdDocumento());
            if ((d.getExtension() == null) || ("".equals(d.getExtension())))
                d.setExtension(ext);
            // FIXME Hay que buscar el documento y despues la pagina
            if ((filename == null) || addPage) {
                DocumentoManager.insertPaginaDocumento(conn, vol, d, "A", 0);
                // Lee nuevamente el documento para actualizar las paginas
                d = DocumentoManager.getDocumento(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), id_carpeta, nombreDocumento);
                filename = d.getFullPathFilesNames()[d.getFullPathFilesNames().length - 1];
            }
            fos = new FileOutputStream(filename);
            int fileLength = 0, length = 0;
            byte[] buffer = new byte[4 * 1024];
            while ((in != null) && ((length = in.read(buffer)) != -1)) {
                fos.write(buffer, 0, length);
                fileLength += length;
            }
            String lowerFilename = nombreDocumento.toLowerCase() + ".xml";
            String[] filenames = d.getFilesNames();
            for (int i = 0; i < filenames.length; i++) {
                if (lowerFilename.equals(filenames[i].toLowerCase())) {
                    Pagina p = d.getPaginaDocumento(i);
                    p.setTamanoBytes(fileLength);
                    PaginaManager.updatePagina(conn, p);
                    break;
                }
            }
            // El Tipo de Caso tiene interface declarada
            TipoCasoInterface tci = null;
            if (c.getTipoCaso().tieneInterface()) {
                tci = instanceTipoCasoInterface(c.getTipoCaso().getInterface());
                tci.onRecibeDocumento(conn, c, d, true);
            }
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("Actualizando Documento Gestion", exc);
            throw new GestionException(exc);
        } catch (IOException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("Actualizando Documento Gestion", exc);
            throw new GestionException(exc);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("General", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            try {
                if (fos != null)
                    fos.close();
                if (in != null)
                    in.close();
            } catch (IOException exc) {
                log.warn("Cerrando archivo " + c.getFolio() + ".xml", exc);
            }
            fos = null;
            in = null;
            conn = null;
        }
    }

    public synchronized void recibeDocumentoGestionPrecompromiso(Caso c, int id_carpeta, String nombreDocumento, String ext, DataInputStream in, boolean addPage) throws GestionException {
        Connection conn = null;
        FileOutputStream fos = null;
        try {
            conn = getConnection();
            Volumen vol = VolumenManager.getVolumen(conn);
            Documento d = DocumentoManager.getDocumentoPrecompromiso(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete());
            if (d == null) {
                d = new Documento();
                d.setTituloAplicacion(c.getTipoCaso().getGavetaAsociada());
                d.setIdGabinete(c.getIdGabinete());
                d.setIdCarpetaPadre(CarpetaManager.getIdCarpetaPadre(conn, d.getTituloAplicacion(), d.getIdGabinete()));
                // FIXME Se debe identificar el tipo de documento
                if ("imx".equals(ext))
                    d.setNombreTipoDocto("IMAX_FILE");
                else
                    d.setNombreTipoDocto("EXTERNO");
                d.setNombreDocumento(nombreDocumento);
                d.setNombreUsuario(c.getCasoOperacion(0).getResponsable());
                d.setExtension(ext);
                DocumentoManager.insertDocumento(conn, d);
                DocumentoManager.insertPaginaDocumento(conn, vol, d, "A", 0);
                // Lee nuevamente el documento para actualizar las paginas
                d = DocumentoManager.getDocumento(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), id_carpeta, nombreDocumento);
            }
            String[] res = AplicacionManager.selectRutaArchivoPreComp(conn, "" + c.getIdCaso(), c.getIdGabinete());
            File archivoActual = null;
            try {
                archivoActual = new File(res[1]);
                archivoActual.delete();
                if (archivoActual.exists())
                    archivoActual.deleteOnExit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String filename = res[1];
            if ((d.getExtension() == null) || ("".equals(d.getExtension())))
                d.setExtension(ext);
            // FIXME Hay que buscar el documento y despues la pagina
            if ((filename == null)) {
                DocumentoManager.insertPaginaDocumento(conn, vol, d, "A", 0);
                // Lee nuevamente el documento para actualizar las paginas
                // d = DocumentoManager.getDocumento(conn,
                // c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(),
                // id_carpeta, nombreDocumento);
                // filename =
                // d.getFullPathFilesNames()[d.getFullPathFilesNames().length -
                // 1];
                res = AplicacionManager.selectRutaArchivoPreComp(conn, "" + c.getIdCaso(), c.getIdGabinete());
                filename = res[1];
            }
            fos = new FileOutputStream(filename);
            int fileLength = 0, length = 0;
            byte[] buffer = new byte[4 * 1024];
            while ((in != null) && ((length = in.read(buffer)) != -1)) {
                fos.write(buffer, 0, length);
                fileLength += length;
            }
            String lowerFilename = nombreDocumento.toLowerCase() + ".xml";
            String[] filenames = d.getFilesNames();
            for (int i = 0; i < filenames.length; i++) {
                if (lowerFilename.equals(filenames[i].toLowerCase())) {
                    Pagina p = d.getPaginaDocumento(i);
                    p.setTamanoBytes(fileLength);
                    PaginaManager.updatePagina(conn, p);
                    break;
                }
            }
            // El Tipo de Caso tiene interface declarada
            TipoCasoInterface tci = null;
            if (c.getTipoCaso().tieneInterface()) {
                tci = instanceTipoCasoInterface(c.getTipoCaso().getInterface());
                tci.onRecibeDocumento(conn, c, d, true);
            }
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("Actualizando Documento Gestion", exc);
            throw new GestionException(exc);
        } catch (IOException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("Actualizando Documento Gestion", exc);
            throw new GestionException(exc);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("General", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            try {
                if (fos != null)
                    fos.close();
                if (in != null)
                    in.close();
            } catch (IOException exc) {
                log.warn("Cerrando archivo " + c.getFolio() + ".xml", exc);
            }
            fos = null;
            in = null;
            conn = null;
        }
    }

    public synchronized void recibeDocumentoGestionPreCompromiso(Caso c, DataInputStream in, String ext) throws GestionException {
        recibeDocumentoGestionPrecompromiso(c, 0, c.getFolio(), ext, in, false);
    }

    public synchronized void recibeDocumentoGestionRequisicion(Caso c, String nombreDocumento, String ext, DataInputStream in) throws GestionException {
        Connection conn = null;
        FileOutputStream fos = null;
        try {
            conn = getConnection();
            Volumen vol = VolumenManager.getVolumen(conn);
            Documento d = DocumentoManager.getDocumentoRequisicion(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), nombreDocumento);
            if (d == null) {
                d = new Documento();
                d.setTituloAplicacion(c.getTipoCaso().getGavetaAsociada());
                d.setIdGabinete(c.getIdGabinete());
                d.setIdCarpetaPadre(CarpetaManager.getIdCarpetaPadre(conn, d.getTituloAplicacion(), d.getIdGabinete()));
                // FIXME Se debe identificar el tipo de documento
                if ("imx".equals(ext))
                    d.setNombreTipoDocto("IMAX_FILE");
                else
                    d.setNombreTipoDocto("EXTERNO");
                d.setNombreDocumento(nombreDocumento);
                d.setNombreUsuario(c.getCasoOperacion(0).getResponsable());
                d.setExtension(ext);
                DocumentoManager.insertDocumento(conn, d);
                DocumentoManager.insertPaginaDocumento(conn, vol, d, "A", 0);
                // Lee nuevamente el documento para actualizar las paginas
                d = DocumentoManager.getDocumentoRequisicion(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), nombreDocumento);
            }
            // String res[] =
            // AplicacionManager.selectRutaArchivoPreComp(conn,""+c.getIdCaso());
            /*
			 * File archivoActual = null; try{ archivoActual = new File(res[1]);
			 * archivoActual.delete();
			 * 
			 * if(archivoActual.exists()) archivoActual.deleteOnExit(); }
			 * catch(Exception e){ e.printStackTrace(); }
			 */
            // res[1];
            String filename = null;
            if ((d.getExtension() == null) || ("".equals(d.getExtension())))
                d.setExtension(ext);
            // FIXME Hay que buscar el documento y despues la pagina
            if ((filename == null)) {
                DocumentoManager.insertPaginaDocumento(conn, vol, d, "A", 0);
                // Lee nuevamente el documento para actualizar las paginas
                d = DocumentoManager.getDocumentoRequisicion(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), nombreDocumento);
                filename = d.getFullPathFilesNames()[d.getFullPathFilesNames().length - 1];
            }
            fos = new FileOutputStream(filename);
            int fileLength = 0, length = 0;
            byte[] buffer = new byte[4 * 1024];
            while ((in != null) && ((length = in.read(buffer)) != -1)) {
                fos.write(buffer, 0, length);
                fileLength += length;
            }
            String lowerFilename = nombreDocumento.toLowerCase() + ".xml";
            String[] filenames = d.getFilesNames();
            for (int i = 0; i < filenames.length; i++) {
                if (lowerFilename.equals(filenames[i].toLowerCase())) {
                    Pagina p = d.getPaginaDocumento(i);
                    p.setTamanoBytes(fileLength);
                    PaginaManager.updatePagina(conn, p);
                    break;
                }
            }
            /*
			 * // El Tipo de Caso tiene interface declarada TipoCasoInterface
			 * tci = null; if (c.getTipoCaso().tieneInterface()){ tci =
			 * instanceTipoCasoInterface(c.getTipoCaso().getInterface());
			 * tci.onRecibeDocumento(conn, c, d, true); }
			 */
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("Actualizando Documento Gestion", exc);
            throw new GestionException(exc);
        } catch (IOException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("Actualizando Documento Gestion", exc);
            throw new GestionException(exc);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("General", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            try {
                if (fos != null)
                    fos.close();
                if (in != null)
                    in.close();
            } catch (IOException exc) {
                log.warn("Cerrando archivo " + c.getFolio() + ".xml", exc);
            }
            fos = null;
            in = null;
            conn = null;
        }
    }

    public void revisaTiempoLimiteDeCasos(String prefixPath) throws GestionException {
        Connection conn = null;
        try {
            conn = getConnection();
            CasoManager.revisaTiempoLimiteDeCasos(conn, prefixPath);
            conn.commit();
        } catch (IOException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("Revisando Tiempo Limite de Casos", exc);
            throw new GestionException(exc);
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("En rollback", ex);
            }
            log.error("Revisando Tiempo Limite de Casos", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public List validaCasoDatos(Caso c, Map data) throws GestionException {
        List errList = new ArrayList();
        Connection conn = null;
        try {
            conn = getConnection();
            Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
            for (Iterator iter = c.getCasoDato().keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                CasoDato cd = c.getCasoDato(name);
                if (!cd.getTipoCasoVariable().isEnGaveta())
                    continue;
                String value = (String) data.get(name);
                Descripcion d = app.getCamposDescripcion(name);
                if (d.isRequerido() && "".equals(value))
                    errList.add("La variable del caso \"" + name + "\" es requerida");
            }
        } catch (SQLException exc) {
            log.error("Validando Caso Datos", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return errList;
    }

    public String versionaDocumento(BitacoraOperacionDoctosBusinessLogic bodbl, String nodeId) throws GestionException {
        String doc_name = null;
        Connection conn = null;
        try {
            conn = getConnection();
            doc_name = versionaDocumento(conn, bodbl, nodeId);
            conn.commit();
        } catch (Exception exc) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    log.warn("En rollback", ex);
                }
            log.error("Borrando pagina", exc);
            throw new GestionException(exc);
        } finally {
            CloseObject.closeObject(conn);
        }
        return doc_name;
    }

    public String versionaDocumento(Connection conn, BitacoraOperacionDoctosBusinessLogic bodbl, String nodeId) throws Exception {
        Fortimax f = new Fortimax(nodeId);
        if (f.getIdDocumento() < 0)
            throw new Exception("No se puede limpiar una carpeta.");
        Documento d = DocumentoManager.selectDocumento(conn, f.getTituloAplicacion(), f.getIdGabinete(), f.getIdCarpeta(), f.getIdDocumento());
        if (d.getEsVersion() > 0)
            throw new Exception("No se puede limpiar una version.");
        Carpeta c = CarpetaManager.getCarpeta(conn, f.getTituloAplicacion(), f.getIdGabinete(), f.getIdCarpeta());
        if ("CFDI".equalsIgnoreCase(c.getNombreCarpeta()))
            throw new Exception("No se pueden modificar facturas. Solicite apoyo con el administrador.");
        int versionDocumento = DocumentoVersionManager.siguienteVersionDocumento(conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), d.getNombreDocumento());
        String nombreDocumento = d.getNombreDocumento();
        String nombreDocumentoNuevo = nombreDocumento + "_" + "V" + String.valueOf(versionDocumento);
        DocumentoManager.cambiaNombreDocumento(conn, f.getTituloAplicacion(), f.getIdGabinete(), f.getIdCarpeta(), f.getIdDocumento(), nombreDocumentoNuevo);
        DocumentoManager.marcaDocumentoVersion(conn, d);
        d.setIdDocumento(-1);
        d.setNumeroPaginas(0);
        d.setNumeroAccesos(0);
        d.setTamanoBytes(0);
        d.setEsVersion(0);
        DocumentoManager.insertDocumento(conn, d);
        String doc_name = d.getNombreDocumento();
        if ("CONCILIABANCOS".equalsIgnoreCase(d.getTituloAplicacion()))
            try {
                ucbl.versionMes(d.getIdGabinete(), nombreDocumento, c.getNombreCarpeta());
            } catch (Exception e) {
                log.error("Error versionando conciliacion bancaria firmada: " + e, e);
            }
        bodbl.insertaBitacora(11, "Se genero la version " + versionDocumento + " del documento " + nombreDocumento + "[" + nodeId + "]");
        return doc_name;
    }
}
