package com.axtel.sisecop.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import com.axtel.sisecop.entities.ProyectoServicio;
import com.axtel.sisecop.repostories.ProductRepository;
import com.axtel.sisecop.repostories.ProjectActivityRepository;
import com.axtel.sisecop.repostories.ProjectBudgetRepository;
import com.axtel.sisecop.repostories.ProjectObservationsRepository;
import com.axtel.sisecop.repostories.ProjectPaymentRepository;
import com.axtel.sisecop.repostories.ProjectProcurementProcessRepository;
import com.axtel.sisecop.repostories.ProyectoConfidencialidadRepositorio;
import com.axtel.sisecop.repostories.ProyectoEstatusRepositorio;
import com.axtel.sisecop.repostories.ProyectoServicioRepositorio;
import com.axtel.sisecop.repostories.ProyectoTerritorioRepositorio;
import com.axtel.sisecop.repostories.ProyectoTipoRepositorio;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import java.util.Base64;

public abstract class ProyectoServicioGeneral extends DataSourceManager {

    private static final FolioGeneratorInterface FOLIO_GENERATOR = Util.getFolioGenerator(GestionInterface.FOLIO_GENERATOR);

    private ProjectBudgetRepository budgetRepository;

    private ProyectoConfidencialidadRepositorio confidencialidadRepositorio;

    private ProyectoEstatusRepositorio estatusRepositorio;

    private ProjectObservationsRepository observationReporitory;

    private ProductRepository productRepository;

    private ProjectActivityRepository projectActivityRepository;

    private ProjectPaymentRepository projectPaymentRepository;

    private CFSequenceManager sequenceManager;

    private ProyectoServicioRepositorio servicioRepositorio;

    private ProjectProcurementProcessRepository tdrRepositorio;

    private ProyectoTerritorioRepositorio territorioRepositorio;

    private ProyectoTipoRepositorio tipoRepositorio;

    private ProyectoExcepcionRepository excepcionRepository;

    public ProyectoServicioGeneral() {
        super.init();
        createObjects();
        sequenceManager = CFSequenceManager.getInstance();
    }

    public ProyectoServicioGeneral(String jniName) {
        super.init(jniName);
        createObjects();
        sequenceManager = CFSequenceManager.getInstance(jniName);
    }

    private final void createObjects() {
        this.servicioRepositorio = new ProyectoServicioRepositorio();
        setServicioRepositorio(servicioRepositorio);
        this.confidencialidadRepositorio = new ProyectoConfidencialidadRepositorio();
        this.estatusRepositorio = new ProyectoEstatusRepositorio();
        this.tipoRepositorio = new ProyectoTipoRepositorio();
        this.territorioRepositorio = new ProyectoTerritorioRepositorio();
        this.productRepository = new ProductRepository();
        this.tdrRepositorio = new ProjectProcurementProcessRepository();
        this.projectActivityRepository = new ProjectActivityRepository();
        this.projectPaymentRepository = new ProjectPaymentRepository();
        this.observationReporitory = new ProjectObservationsRepository();
        this.budgetRepository = new ProjectBudgetRepository();
        this.excepcionRepository = new ProyectoExcepcionRepository();
        getServicioRepositorio().setConfidencialidadRepositorio(confidencialidadRepositorio);
        getServicioRepositorio().setEstatusRepositorio(estatusRepositorio);
        getServicioRepositorio().setTipoRepositorio(tipoRepositorio);
        getServicioRepositorio().setTerritorioRepositorio(territorioRepositorio);
        getServicioRepositorio().setProductRepository(productRepository);
        getServicioRepositorio().setTdrRepositorio(tdrRepositorio);
        getServicioRepositorio().setBudgetRepository(budgetRepository);
        getServicioRepositorio().setProjectActivityRepository(projectActivityRepository);
        getServicioRepositorio().setProjectPaymentRepository(projectPaymentRepository);
        getServicioRepositorio().setExcepcionRepositorio(excepcionRepository);
    }

    public int createProyectoServicio(Connection connection, ProyectoServicio proyectoServicio) throws SQLException {
        Caso c = initProcess(connection, proyectoServicio);
        proyectoServicio.setIdProcess(c.getIdCaso());
        proyectoServicio.setServicioModificacion(new Timestamp(System.currentTimeMillis()));
        proyectoServicio.setServicioCreacion(new Timestamp(System.currentTimeMillis()));
        proyectoServicio.setServicioFolioNum(sequenceManager.nextVal(connection, "PROJECT_" + proyectoServicio.getServicioFolioAnio()));
        return getServicioRepositorio().create(connection, proyectoServicio);
    }

    public ProjectBudgetRepository getBudgetRepository() {
        return budgetRepository;
    }

    public ProyectoConfidencialidadRepositorio getConfidencialidadRepositorio() {
        return confidencialidadRepositorio;
    }

    public ProyectoEstatusRepositorio getEstatusRepositorio() {
        return estatusRepositorio;
    }

    public ProjectObservationsRepository getObservationReporitory() {
        return observationReporitory;
    }

    public ProductRepository getProductRepository() {
        return productRepository;
    }

    public ProjectActivityRepository getProjectActivityRepository() {
        return projectActivityRepository;
    }

    public ProjectPaymentRepository getProjectPaymentRepository() {
        return projectPaymentRepository;
    }

    public ProyectoServicioRepositorio getServicioRepositorio() {
        return servicioRepositorio;
    }

    public ProjectProcurementProcessRepository getTdrRepositorio() {
        return tdrRepositorio;
    }

    public ProyectoTerritorioRepositorio getTerritorioRepositorio() {
        return territorioRepositorio;
    }

    public ProyectoTipoRepositorio getTipoRepositorio() {
        return tipoRepositorio;
    }

    public Caso initProcess(Connection conn, ProyectoServicio proyectoServicio) {
        try {
            Usuario u = new Usuario(proyectoServicio.getLoginUsuario());
            u = UsuarioManager.select(conn, u);
            u = UsuarioManager.getRamoUR(conn, u);
            Map<String, String> variables = new HashMap<>();
            return Util.generaCaso(conn, u, 97, FOLIO_GENERATOR, "PROJECT_REGISTER", variables);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setBudgetRepository(ProjectBudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public void setConfidencialidadRepositorio(ProyectoConfidencialidadRepositorio confidencialidadRepositorio) {
        this.confidencialidadRepositorio = confidencialidadRepositorio;
    }

    public void setEstatusRepositorio(ProyectoEstatusRepositorio estatusRepositorio) {
        this.estatusRepositorio = estatusRepositorio;
    }

    public void setObservationReporitory(ProjectObservationsRepository observationReporitory) {
        this.observationReporitory = observationReporitory;
    }

    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void setProjectActivityRepository(ProjectActivityRepository projectActivityRepository) {
        this.projectActivityRepository = projectActivityRepository;
    }

    public void setProjectPaymentRepository(ProjectPaymentRepository projectPaymentRepository) {
        this.projectPaymentRepository = projectPaymentRepository;
    }

    public void setServicioRepositorio(ProyectoServicioRepositorio servicioRepositorio) {
        this.servicioRepositorio = servicioRepositorio;
    }

    public void setTdrRepositorio(ProjectProcurementProcessRepository tdrRepositorio) {
        this.tdrRepositorio = tdrRepositorio;
    }

    public void setTerritorioRepositorio(ProyectoTerritorioRepositorio territorioRepositorio) {
        this.territorioRepositorio = territorioRepositorio;
    }

    public void setTipoRepositorio(ProyectoTipoRepositorio tipoRepositorio) {
        this.tipoRepositorio = tipoRepositorio;
    }
}
