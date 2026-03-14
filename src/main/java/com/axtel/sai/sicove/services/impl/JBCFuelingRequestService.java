package com.axtel.sai.sicove.services.impl;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import com.axtel.contratos.exception.ContratoException;
import com.axtel.sai.sicove.entities.EmployeeDAO;
import com.axtel.sai.sicove.entities.FuelingJustification;
import com.axtel.sai.sicove.entities.Vehicle;
import com.axtel.sai.sicove.entities.VehicleFuelRequest;
import com.axtel.sai.sicove.entities.VehicleFuelRequestDAO;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.EmployeeRepository;
import com.axtel.sai.sicove.repositories.FuelingJustificationRepository;
import com.axtel.sai.sicove.repositories.FuelingRequestRepository;
import com.axtel.sai.sicove.repositories.VehicleRepository;
import com.axtel.sai.sicove.services.FuelingRequestService;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class JBCFuelingRequestService extends DataSourceManager implements FuelingRequestService {

    private static final Logger log = LoggerFactory.getLogger(JBCFuelingRequestService.class);

    private FuelingRequestRepository fuelingRequestRepository;

    private FuelingJustificationRepository fuelingJustificationRepository;

    private VehicleRepository vehicleRepository;

    private EmployeeRepository employeeRepository;

    private static final FolioGeneratorInterface FOLIO_GENERATOR = Util.getFolioGenerator(GestionInterface.FOLIO_GENERATOR);

    public JBCFuelingRequestService(String jniName, FuelingRequestRepository fuelingRequestRepository, FuelingJustificationRepository fuelingJustificationRepository, VehicleRepository vehicleRepository, EmployeeRepository employeeRepository) {
        super.init(jniName);
        this.fuelingRequestRepository = fuelingRequestRepository;
        this.fuelingJustificationRepository = fuelingJustificationRepository;
        this.vehicleRepository = vehicleRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public VehicleFuelRequest saveFuelRequest(VehicleFuelRequest fuelRequest) throws ContratoException {
        Connection conn = null;
        try {
            conn = getConnection();
            FuelingJustification fuelingJustification = fuelingJustificationRepository.saveFuelingJustification(conn, fuelRequest.getJustification());
            Vehicle vehicle = vehicleRepository.findByInventoryId(conn, fuelRequest.getVehicleId());
            Caso c = initProcess(conn, fuelRequest, vehicle);
            fuelRequest.setIdProcess(c.getIdCaso());
            fuelRequest.setJustification(fuelingJustification);
            fuelRequest = fuelingRequestRepository.saveFuelRequest(conn, fuelRequest);
            fuelRequest.setJustification(fuelingJustification);
            conn.commit();
            return fuelRequest;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2, e2);
                }
            throw new ContratoException(e.getMessage(), e.getCause());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public VehicleFuelRequest getFuelRequest(int fuelRequestId) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            VehicleFuelRequest fuelRequest = fuelingRequestRepository.readVehicleFuelRequest(conn, fuelRequestId);
            FuelingJustification fuelingJustification = fuelingJustificationRepository.readFuelingJustification(conn, fuelRequest.getJustificationId());
            fuelRequest.setJustification(fuelingJustification);
            return fuelRequest;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new SicoveException(e.getMessage(), e.getCause());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public Caso initProcess(Connection conn, VehicleFuelRequest fuelRequest, Vehicle vehicle) throws SicoveException {
        try {
            Usuario u = new Usuario(fuelRequest.getUserRequest());
            u = UsuarioManager.select(conn, u);
            u = UsuarioManager.getRamoUR(conn, u);
            Map<String, String> variables = new HashMap<>();
            variables.put("LICENSE_PLATE", vehicle.getLicensePlate());
            return Util.generaCaso(conn, u, 100, FOLIO_GENERATOR, "REQFUELWALLET", variables);
        } catch (Exception e) {
            throw new SicoveException(e);
        }
    }

    @Override
    public VehicleFuelRequest updateFuelRequest(VehicleFuelRequest fuelRequest) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            FuelingJustification fuelingJustificationOrg = fuelingJustificationRepository.readFuelingJustification(conn, fuelRequest.getJustification().getJustificationId());
            fuelingJustificationOrg.setWithJustification(fuelRequest.getJustification().isWithJustification());
            fuelingJustificationOrg.setCountryId(fuelRequest.getJustification().getCountryId());
            fuelingJustificationOrg.setEndDate(fuelRequest.getJustification().getEndDate());
            fuelingJustificationOrg.setIdCommision(fuelRequest.getJustification().getIdCommision());
            fuelingJustificationOrg.setInitialDate(fuelRequest.getJustification().getInitialDate());
            fuelingJustificationOrg.setJustification(fuelRequest.getJustification().getJustification());
            fuelingJustificationOrg.setMunicipalityName(fuelRequest.getJustification().getMunicipalityName());
            fuelingJustificationOrg.setStateName(fuelRequest.getJustification().getStateName());
            fuelingJustificationOrg = fuelingJustificationRepository.updateFuelingJustification(conn, fuelingJustificationOrg);
            VehicleFuelRequest fuelRequestOrig = fuelingRequestRepository.readVehicleFuelRequest(conn, fuelRequest.getFuelingRequestId());
            fuelRequestOrig.setEmployeeResponsible(fuelRequest.getEmployeeResponsible());
            fuelRequestOrig.setEstimatedKilometers(fuelRequest.getEstimatedKilometers());
            fuelRequestOrig.setFuelingAmount(fuelRequest.getFuelingAmount());
            fuelRequestOrig.setIdStatus(fuelRequest.getIdStatus());
            fuelRequestOrig.setVehicleId(fuelRequest.getVehicleId());
            fuelRequestOrig.setWalletNumber(fuelRequest.getWalletNumber());
            fuelRequestOrig.setIdWallet(fuelRequest.getIdWallet());
            fuelRequest = fuelingRequestRepository.updateFuelRequest(conn, fuelRequestOrig);
            fuelRequest.setJustification(fuelingJustificationOrg);
            conn.commit();
            return fuelRequest;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2, e2);
                }
            throw new SicoveException(e.getMessage(), e.getCause());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public void updateFuelRequestStatus(int fuelRequestId, int status) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            fuelingRequestRepository.updateFuelRequestStatus(conn, fuelRequestId, status);
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2, e2);
                }
            throw new SicoveException(e.getMessage(), e.getCause());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public void authRequestStatus(VehicleFuelRequest fuelRequest) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            fuelingRequestRepository.authRequestStatus(conn, fuelRequest);
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2, e2);
                }
            throw new SicoveException(e.getMessage(), e.getCause());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public void finishRequest(VehicleFuelRequest fuelRequest) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            fuelingRequestRepository.finishRequest(conn, fuelRequest);
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2, e2);
                }
            throw new SicoveException(e.getMessage(), e.getCause());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public void discardRequest(VehicleFuelRequest fuelRequest) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            fuelingRequestRepository.discardRequest(conn, fuelRequest);
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2, e2);
                }
            throw new SicoveException(e.getMessage(), e.getCause());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public void rejectRequest(VehicleFuelRequest fuelRequest) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            fuelingRequestRepository.updateFuelRequestStatus(conn, fuelRequest.getFuelingRequestId(), fuelRequest.getIdStatus());
            fuelingRequestRepository.rejectRequest(conn, fuelRequest);
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2, e2);
                }
            throw new SicoveException(e.getMessage(), e.getCause());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public VehicleFuelRequestDAO getFullFuelRequest(int fuelRequestId) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            VehicleFuelRequestDAO fuelRequest = fuelingRequestRepository.readFullVehicleFuelRequest(conn, fuelRequestId);
            FuelingJustification fuelingJustification = fuelingJustificationRepository.readFuelingJustification(conn, fuelRequest.getJustificationId());
            Vehicle vehicle = vehicleRepository.findByInventoryId(conn, fuelRequest.getVehicleId());
            EmployeeDAO responsible = employeeRepository.readEmployee(conn, fuelRequest.getEmployeeResponsible());
            EmployeeDAO applicant = employeeRepository.readEmployeeByLogin(conn, fuelRequest.getUserRequest());
            fuelRequest.setJustification(fuelingJustification);
            fuelRequest.setApplicant(applicant);
            fuelRequest.setVehicle(vehicle);
            fuelRequest.setResponsible(responsible);
            return fuelRequest;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new SicoveException(e.getMessage(), e.getCause());
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
