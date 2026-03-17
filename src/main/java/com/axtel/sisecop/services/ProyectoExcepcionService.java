package com.axtel.sisecop.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.axtel.sisecop.dto.ProductDTO;
import com.axtel.sisecop.dto.ProjectBudgetItemDTO;
import com.axtel.sisecop.dto.ProjectPaymentDTO;
import com.axtel.sisecop.dto.ProyectoExcepcionDTO;
import com.axtel.sisecop.dto.ProyectoServicioActividadDTO;
import com.axtel.sisecop.dto.TerritoryDTO;
import com.axtel.sisecop.entities.ProjectBudgetItem;
import com.axtel.sisecop.entities.ProyectoEstatus;
import com.axtel.sisecop.entities.ProyectoExcepcion;
import com.axtel.sisecop.entities.ProyectoProducto;
import com.axtel.sisecop.entities.ProyectoServicio;
import com.axtel.sisecop.entities.ProyectoServicioActividad;
import com.axtel.sisecop.entities.ProyectoServicioPago;
import com.axtel.sisecop.entities.ProyectoServicioTDR;
import com.axtel.sisecop.entities.ProyectoServicioTerritorio;
import com.axtel.sisecop.entities.TipoExcepcion;
import com.syc.fortimax.core.Fortimax;
import com.syc.fortimax.core.FortimaxManager;
import com.syc.fortimax.exceptions.FortimaxException;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ProyectoExcepcionService extends ProyectoServicioGeneral {

    private static final Logger log = LoggerFactory.getLogger(ProyectoExcepcionService.class);

    private ProyectoExcepcionRepository proyectoExcepcionRepository;

    public ProyectoExcepcionService(String jniName) {
        super(jniName);
    }

    public ProyectoServicio creaCopiaInicial(ProyectoExcepcionDTO proyectoExcepcionDTO) {
        Connection conn = null;
        int consecutivo = 0;
        try {
            conn = getConnection();
            consecutivo = proyectoExcepcionRepository.getMaxConsecutivo(conn, proyectoExcepcionDTO.getIdProyectoOriginal(), proyectoExcepcionDTO.getIdTipoExcepcion()) + 1;
            TipoExcepcion tipoEx = TipoExcepcion.findById(proyectoExcepcionDTO.getIdTipoExcepcion());
            ProyectoServicio proyecto = getServicioRepositorio().findById(conn, proyectoExcepcionDTO.getIdProyectoOriginal());
            proyecto.setServicioTitulo(proyectoExcepcionDTO.getTituloProyecto());
            proyecto.setLoginUsuario(proyectoExcepcionDTO.getLogin());
            proyecto.setEstatus(new ProyectoEstatus(1));
            proyecto.setServicioCompleto(false);
            int nuevoID = clonarProyecto(conn, proyecto);
            proyecto.setServicioId(nuevoID);
            clonarActividades(conn, proyecto);
            clonarServicioPagos(conn, proyecto);
            clonarServicioProductos(conn, proyecto);
            clonarServicioClaves(conn, proyecto);
            clonarServicioTDR(conn, proyecto);
            clonarServicioTerritorios(conn, proyecto);
            ProyectoExcepcion proyectoExcepcion = new ProyectoExcepcion();
            proyectoExcepcion.setTipoExcepcion(tipoEx);
            proyectoExcepcion.setActivo("S");
            proyectoExcepcion.setConsecutivo(consecutivo);
            proyectoExcepcion.setIdHijo(nuevoID);
            proyectoExcepcion.setIdProyecto(proyectoExcepcionDTO.getIdProyectoOriginal());
            proyectoExcepcionRepository.insertProyectoExcepcion(conn, proyectoExcepcion);
            proyecto = getServicioRepositorio().findById(conn, nuevoID);
            conn.commit();
            return proyecto;
        } catch (Exception e) {
            Util.rollback(conn);
            throw new RuntimeException(e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private int clonarProyecto(Connection conn, ProyectoServicio proyecto) throws Exception {
        String centralUnit = ConfiguraAplicativoManager.getSystemSetting(conn, "UNIDAD_CONTABLE");
        int fiscalYear = Integer.parseInt(EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal());
        proyecto.setServicioFolioAnio(fiscalYear);
        proyecto.setServicioFolioPre(centralUnit);
        return createProyectoServicio(conn, proyecto);
    }

    private void clonarActividades(Connection conn, ProyectoServicio proyecto) throws SQLException {
        log.info("Object: {}", "Clonando actividades del proyecto ID: " + proyecto.getServicioId() + " Se clonaran: " + proyecto.getActividades() == null ? "0" : proyecto.getActividades().size() + " actividades");
        for (ProyectoServicioActividad actividad : proyecto.getActividades()) {
            log.trace("Object: {}", "Clonando actividad : " + actividad);
            ProyectoServicioActividadDTO dto = new ProyectoServicioActividadDTO();
            dto.setIdService(proyecto.getServicioId());
            dto.setServicioactividadAnio(actividad.getServicioactividadAnio());
            dto.setServicioactividadDescripcion(actividad.getServicioactividadDescripcion());
            dto.setSisecopMes(actividad.getSisecopMes());
            getProjectActivityRepository().insertProyectoServicioActividad(conn, dto);
            log.trace("Object: {}", "Actividad [" + actividad + "] clonada exitosamente");
        }
        log.debug("Object: " + String.valueOf("Clonando actividades del proyecto ID: " + proyecto.getServicioId() + " Terminado"));
    }

    private void clonarServicioPagos(Connection conn, ProyectoServicio proyecto) throws SQLException {
        log.info("Object: {}", "Clonando pagos del proyecto ID: " + proyecto.getServicioId() + " Se clonarán: " + (proyecto.getPagos() == null ? "0" : proyecto.getPagos().size()) + " pagos");
        for (ProyectoServicioPago pago : proyecto.getPagos()) {
            log.trace("Object: {}", "Clonando pago: " + pago);
            ProjectPaymentDTO dto = new ProjectPaymentDTO();
            dto.setIdService(proyecto.getServicioId());
            dto.setMesPago(pago.getMesPago());
            dto.setServicioPagoAnio(pago.getServicioPagoAnio());
            dto.setServicioPagoCantidad(pago.getServicioPagoCantidad());
            dto.setServicioPagoId(pago.getServicioPagoId());
            getProjectPaymentRepository().create(conn, dto);
            log.trace("Object: {}", "Pago [" + pago + "] clonado exitosamente");
        }
        log.debug("Object: " + String.valueOf("Clonación de pagos del proyecto ID: " + proyecto.getServicioId() + " terminada"));
    }

    private void clonarServicioProductos(Connection conn, ProyectoServicio proyecto) throws SQLException {
        log.info("Object: {}", "Clonando productos del proyecto ID: " + proyecto.getServicioId() + ". Se clonarán: " + (proyecto.getProductos() == null ? "0" : proyecto.getProductos().size()) + " productos");
        for (ProyectoProducto producto : proyecto.getProductos()) {
            log.trace("Object: {}", "Clonando producto: " + producto);
            ProductDTO dto = new ProductDTO();
            dto.setDescripcion(producto.getProductoDescripcion());
            dto.setProductoId(producto.getProductoId());
            dto.setServicioId(proyecto.getServicioId());
            dto.setServicioproductoId(producto.getServicioProductoID());
            getProductRepository().insertProduct(conn, dto);
            log.trace("Object: {}", "Producto [" + producto + "] clonado exitosamente");
        }
        log.debug("Object: " + String.valueOf("Clonación de productos del proyecto ID: " + proyecto.getServicioId() + " terminada"));
    }

    private void clonarServicioClaves(Connection conn, ProyectoServicio proyecto) throws SQLException {
        log.info("Object: {}", "Clonando claves presupuestarias del proyecto ID: " + proyecto.getServicioId() + ". Se clonarán: " + (proyecto.getServicioClaves() == null ? "0" : proyecto.getServicioClaves().size()) + " claves presupuestarias");
        for (ProjectBudgetItem clave : proyecto.getServicioClaves()) {
            log.trace("Object: {}", "Clonando clave presupuestaria: " + clave);
            ProjectBudgetItemDTO dto = new ProjectBudgetItemDTO();
            dto.setAdministrativeUnit(clave.getAdministrativeUnit());
            dto.setBudgetItem(clave.getBudgetItem());
            dto.setEndYear(clave.getEndYear());
            dto.setIdService(proyecto.getServicioId());
            dto.setInitialYear(clave.getInitialYear());
            dto.setManagement(clave.getManagement());
            getBudgetRepository().create(conn, dto);
            log.trace("Object: {}", "Clave presupuestaria [" + clave + "] clonada exitosamente");
        }
        log.debug("Object: " + String.valueOf("Clonación de claves presupuestarias del proyecto ID: " + proyecto.getServicioId() + " terminada"));
    }

    private void clonarServicioTDR(Connection conn, ProyectoServicio proyecto) throws SQLException, FortimaxException {
        log.info("Object: {}", "Clonando TDR del proyecto ID: " + proyecto.getServicioId() + ". Se clonarán: " + (proyecto.getServiciosTDR() == null ? "0" : proyecto.getServiciosTDR().size()) + " TDRs");
        for (ProyectoServicioTDR tdr : proyecto.getServiciosTDR()) {
            log.trace("Object: {}", "Clonando TDR: " + tdr);
            if (Fortimax.esNodoValido(tdr.getTdrRuta())) {
                Fortimax source = new Fortimax(tdr.getTdrRuta());
                Fortimax copy = FortimaxManager.copyFile(conn, source, proyecto.getLoginUsuario(), source.getTituloAplicacion(), proyecto.getIdProcess());
                tdr.setTdrRuta(copy.toString());
            }
            tdr.setTdrId(0);
            getTdrRepositorio().createServicioTermino(conn, proyecto.getServicioId(), tdr);
            log.trace("Object: {}", "TDR [" + tdr + "] clonado exitosamente");
        }
        log.debug("Object: " + String.valueOf("Clonación de TDRs del proyecto ID: " + proyecto.getServicioId() + " terminada"));
    }

    private void clonarServicioTerritorios(Connection conn, ProyectoServicio proyecto) throws SQLException {
        log.info("Object: {}", "Clonando territorios del proyecto ID: " + proyecto.getServicioId() + ". Se clonarán: " + (proyecto.getTerritorios() == null ? "0" : proyecto.getTerritorios().size()) + " territorios");
        for (ProyectoServicioTerritorio territorio : proyecto.getTerritorios()) {
            log.trace("Object: {}", "Clonando territorio: " + territorio);
            // Crear un DTO a partir del POJO
            TerritoryDTO dto = new TerritoryDTO();
            dto.setMunicipalityId(territorio.getMunicipio().getMunicipioId());
            dto.setServicioId(proyecto.getServicioId());
            dto.setStateId(territorio.getEntidadFederativa().getId());
            // Insertar el territorio clonado
            getTerritorioRepositorio().create(conn, dto);
            log.trace("Object: {}", "Territorio [" + territorio + "] clonado exitosamente");
        }
        log.debug("Object: " + String.valueOf("Clonación de territorios del proyecto ID: " + proyecto.getServicioId() + " terminada"));
    }

    public ProyectoExcepcion createProyectoExcepcion(ProyectoExcepcion proyectoExcepcion) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            ProyectoExcepcion insertedExcepcion = proyectoExcepcionRepository.insertProyectoExcepcion(connection, proyectoExcepcion);
            connection.commit();
            return insertedExcepcion;
        } catch (SQLException e) {
            if (connection != null)
                connection.rollback();
            throw e;
        } finally {
            if (connection != null)
                connection.close();
        }
    }

    public void deleteProyectoExcepcion(int idProyecto, int idHijo) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            proyectoExcepcionRepository.deleteProyectoExcepcion(connection, idProyecto, idHijo);
            connection.commit();
        } catch (SQLException e) {
            if (connection != null)
                connection.rollback();
            throw e;
        } finally {
            if (connection != null)
                connection.close();
        }
    }

    public List<ProyectoExcepcion> findAllProyectoExcepciones() throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return proyectoExcepcionRepository.findAllProyectoExcepcion(connection);
        } finally {
            if (connection != null)
                connection.close();
        }
    }

    public ProyectoExcepcion findProyectoExcepcionById(int idProyecto, int idHijo) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return proyectoExcepcionRepository.findProyectoExcepcionById(connection, idProyecto, idHijo);
        } finally {
            if (connection != null)
                connection.close();
        }
    }

    public int getMaxConsecutivo(int idProyecto, int idHijo) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return proyectoExcepcionRepository.getMaxConsecutivo(connection, idProyecto, idHijo);
        } finally {
            if (connection != null)
                connection.close();
        }
    }

    public ProyectoExcepcionRepository getProyectoExcepcionRepository() {
        return proyectoExcepcionRepository;
    }

    public void setProyectoExcepcionRepository(ProyectoExcepcionRepository proyectoExcepcionRepository) {
        this.proyectoExcepcionRepository = proyectoExcepcionRepository;
    }

    public ProyectoExcepcion updateProyectoExcepcion(ProyectoExcepcion proyectoExcepcion) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            ProyectoExcepcion updatedExcepcion = proyectoExcepcionRepository.updateProyectoExcepcion(connection, proyectoExcepcion);
            connection.commit();
            return updatedExcepcion;
        } catch (SQLException e) {
            if (connection != null)
                connection.rollback();
            throw e;
        } finally {
            if (connection != null)
                connection.close();
        }
    }
}
