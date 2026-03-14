package com.axtel.proveedores;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.lang.StringUtils;
import com.axtel.contratos.core.ContratoDiverso;
import com.axtel.proveedores.dao.ProveedorDAO;
import com.axtel.proveedores.exception.ProveedorException;
import com.axtel.proveedores.model.Proveedor;
import com.syc.altaproveedor.AltaProveedorManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.CarpetaManager;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoBussinessLogic;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.OrgCarpeta;
import com.syc.fortimax.core.OrgCarpetaManager;
import com.syc.fortimax.exceptions.FortimaxException;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProveedorService extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ProveedorService.class);

    public ProveedorService() {
    }

    public ProveedorService(String jniName) {
        super.init(jniName);
    }

    public Proveedor selectByRfc(String rfc) throws ProveedorException {
        Connection conn = null;
        try {
            conn = getConnection();
            return ProveedorDAO.selectByRfc(conn, rfc);
        } catch (Exception e) {
            throw new ProveedorException("Error leyendo proveedor de la base de datos: " + e, e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int updateProveedor(Proveedor proveedor) throws ProveedorException {
        Connection conn = null;
        try {
            conn = getConnection();
            int actualizados = ProveedorDAO.update(conn, proveedor);
            conn.commit();
            return actualizados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                }
            throw new ProveedorException("Error actualizando proveedor: " + e, e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int updateREPSEProveedor(Proveedor proveedor, Usuario u, File evidencia) throws ProveedorException {
        Connection conn = null;
        int actualizados = 0;
        try {
            conn = getConnection();
            Caso c = AltaProveedorManager.getTramiteProveedor(conn, proveedor.getRfc());
            Carpeta cPadre = CarpetaManager.getCarpetaByName(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), "REPSE");
            if (cPadre == null) {
                cPadre = Util.creaCarpeta(conn, c, u, "REPSE", "Evidencia del Registro");
            }
            Documento d = DocumentoManager.getDocumento(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), cPadre.getIdCarpeta(), "Evidencia STPS");
            if (d == null) {
                d = Util.creaDocumento(conn, c, cPadre, u, "Evidencia STPS", "Documento evidencia del registro ante la STPS");
            } else if (d != null && d.getPaginasDocumento() != null && d.getPaginasDocumento().length > 0)
                d = DocumentoBussinessLogic.versionaDocumento(conn, u, d);
            DocumentoManager.insertPaginaDocumento(conn, d, evidencia);
            actualizados = ProveedorDAO.update(conn, proveedor);
            conn.commit();
            return actualizados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2, e2);
                }
            throw new ProveedorException("Error actualizando proveedor: " + e, e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public static void convertRESICO(Connection conn, ContratoDiverso diverseContract) throws SQLException {
        ProveedorDAO.convertRESICO(conn, diverseContract.getRfc());
        ProveedorDAO.convertSupplierRESICO(conn, diverseContract.getRfc());
    }
}
