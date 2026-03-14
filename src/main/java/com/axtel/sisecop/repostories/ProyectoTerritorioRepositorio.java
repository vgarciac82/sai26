package com.axtel.sisecop.repostories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.axtel.sisecop.dto.TerritoryDTO;
import com.axtel.sisecop.entities.ProyectoEntidadFederativa;
import com.axtel.sisecop.entities.ProyectoMunicipio;
import com.axtel.sisecop.entities.ProyectoServicioTerritorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProyectoTerritorioRepositorio {

    private static final Logger log = LoggerFactory.getLogger(ProyectoTerritorioRepositorio.class);

    public int create(Connection conn, TerritoryDTO territoryDTO) throws SQLException {
        String sql = "INSERT INTO sisecop_serviciosterritorios (servicioId, entidadId, municipioId) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, territoryDTO.getServicioId());
            stmt.setInt(2, territoryDTO.getStateId());
            stmt.setInt(3, territoryDTO.getMunicipalityId());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    log.info("Object: {}", "Territory created successfully with ID: " + generatedId);
                    return generatedId;
                } else {
                    throw new SQLException("Creating territory failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error creating territory: " + e.getMessage(), e);
        }
    }

    public ProyectoServicioTerritorio readById(Connection conn, int servicioterritorioId) throws SQLException {
        String sql = "SELECT st.servicioterritorioId, st.servicioId, st.entidadId, st.municipioId, " + "e.entidadNombre, m.municipioNombre " + "FROM dbo.sisecop_serviciosterritorios st " + "JOIN dbo.sisecop_entidades e ON st.entidadId = e.entidadId " + "JOIN dbo.sisecop_municipios m ON st.municipioId = m.municipioId " + "WHERE st.servicioterritorioId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, servicioterritorioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapToTerritorio(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error reading territory by ID: " + e.getMessage(), e);
        }
    }

    private ProyectoServicioTerritorio mapToTerritorio(ResultSet rs) throws SQLException {
        ProyectoServicioTerritorio territorio = new ProyectoServicioTerritorio();
        ProyectoEntidadFederativa entidad = new ProyectoEntidadFederativa();
        entidad.setId(rs.getInt("entidadId"));
        entidad.setNombre(rs.getString("entidadNombre"));
        ProyectoMunicipio municipio = new ProyectoMunicipio();
        municipio.setMunicipioId(rs.getInt("municipioId"));
        municipio.setEntidadFederativaId(rs.getInt("entidadId"));
        municipio.setMunicipioNombre(rs.getString("municipioNombre"));
        territorio.setEntidadFederativa(entidad);
        territorio.setMunicipio(municipio);
        territorio.setTerritorioId(rs.getInt("servicioterritorioId"));
        return territorio;
    }

    public List<ProyectoServicioTerritorio> readByServicioId(Connection conn, int servicioId) throws SQLException {
        log.info("Object: {}", "Looking for territory scope by the project ID " + servicioId);
        List<ProyectoServicioTerritorio> territorios = new ArrayList<>();
        String sql = "SELECT st.servicioterritorioId, st.entidadId, st.municipioId, " + "e.entidadNombre, m.municipioNombre " + "FROM dbo.sisecop_serviciosterritorios st " + "JOIN dbo.sisecop_entidades e ON st.entidadId = e.entidadId " + "JOIN dbo.sisecop_municipios m ON st.municipioId = m.municipioId " + "WHERE st.servicioId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, servicioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    territorios.add(mapToTerritorio(rs));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error reading territories by servicioId: " + e.getMessage(), e);
        }
        return territorios;
    }

    public int delete(Connection connection, int idTerritory) throws SQLException {
        String sql = "DELETE FROM sisecop_serviciosterritorios WHERE servicioterritorioId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idTerritory);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error creating territory: " + e.getMessage(), e);
        }
    }
}
