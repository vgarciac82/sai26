package com.axtel.sisecop.repostories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.axtel.sisecop.dto.ProjectBudgetItemDTO;
import com.axtel.sisecop.entities.ProjectBudgetItem;
import java.util.Base64;

public class ProjectBudgetRepository {

    private static final String INSERT_SQL = "INSERT INTO [dbo].[sisecop_serviciosclaves] (servicioId, servicioclaveAnioIni, servicioclaveAnioFin, servicioclaveUnidad, servicioclaveGerencia, servicioclavePartida) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_ID_SQL = "SELECT * FROM [dbo].[sisecop_serviciosclaves] WHERE servicioclaveId = ?";

    private static final String SELECT_BY_SERVICE_ID_SQL = "SELECT * FROM [dbo].[sisecop_serviciosclaves] WHERE servicioId = ?";

    private static final String SELECT_ALL_SQL = "SELECT * FROM [dbo].[sisecop_serviciosclaves]";

    private static final String UPDATE_SQL = "UPDATE [dbo].[sisecop_serviciosclaves] SET servicioId = ?, servicioclaveAnioIni = ?, servicioclaveAnioFin = ?, servicioclaveUnidad = ?, servicioclaveGerencia = ?, servicioclavePartida = ? WHERE servicioclaveId = ?";

    private static final String DELETE_SQL = "DELETE FROM [dbo].[sisecop_serviciosclaves] WHERE servicioclaveId = ?";

    public ProjectBudgetItem create(Connection connection, ProjectBudgetItemDTO dto) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, dto.getIdService());
            preparedStatement.setInt(2, dto.getInitialYear());
            preparedStatement.setInt(3, dto.getEndYear());
            preparedStatement.setString(4, dto.getAdministrativeUnit());
            preparedStatement.setString(5, dto.getManagement());
            preparedStatement.setString(6, dto.getBudgetItem());
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    return selectProyectoServicioClaveById(connection, generatedId);
                } else
                    throw new SQLException("Se insertó el registro pero no es posible recuperar el ID");
            }
        }
    }

    public ProjectBudgetItem selectProyectoServicioClaveById(Connection connection, int id) throws SQLException {
        ProjectBudgetItem proyectoServicioClave = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                proyectoServicioClave = mapToProyectoServicioClave(rs);
            }
        }
        return proyectoServicioClave;
    }

    public List<ProjectBudgetItem> readByServicioId(Connection connection, int serviceId) throws SQLException {
        List<ProjectBudgetItem> proyectoServicioClaveList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_SERVICE_ID_SQL)) {
            preparedStatement.setInt(1, serviceId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                proyectoServicioClaveList.add(mapToProyectoServicioClave(rs));
            }
        }
        return proyectoServicioClaveList;
    }

    public List<ProjectBudgetItem> selectAllProyectoServicioClave(Connection connection) throws SQLException {
        List<ProjectBudgetItem> proyectoServicioClaveList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SQL)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                proyectoServicioClaveList.add(mapToProyectoServicioClave(rs));
            }
        }
        return proyectoServicioClaveList;
    }

    public boolean updateProyectoServicioClave(Connection connection, ProjectBudgetItem proyectoServicioClave) throws SQLException {
        boolean rowUpdated;
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setInt(1, proyectoServicioClave.getId());
            preparedStatement.setInt(2, proyectoServicioClave.getInitialYear());
            preparedStatement.setInt(3, proyectoServicioClave.getEndYear());
            preparedStatement.setString(4, proyectoServicioClave.getAdministrativeUnit());
            preparedStatement.setString(5, proyectoServicioClave.getManagement());
            preparedStatement.setString(6, proyectoServicioClave.getBudgetItem());
            preparedStatement.setInt(7, proyectoServicioClave.getId());
            rowUpdated = preparedStatement.executeUpdate() > 0;
        }
        return rowUpdated;
    }

    public boolean deleteProyectoServicioClave(Connection connection, int id) throws SQLException {
        boolean rowDeleted;
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, id);
            rowDeleted = preparedStatement.executeUpdate() > 0;
        }
        return rowDeleted;
    }

    private ProjectBudgetItem mapToProyectoServicioClave(ResultSet rs) throws SQLException {
        ProjectBudgetItem proyectoServicioClave = new ProjectBudgetItem();
        proyectoServicioClave.setId(rs.getInt("servicioclaveId"));
        proyectoServicioClave.setInitialYear(rs.getInt("servicioclaveAnioIni"));
        proyectoServicioClave.setEndYear(rs.getInt("servicioclaveAnioFin"));
        proyectoServicioClave.setAdministrativeUnit(rs.getString("servicioclaveUnidad"));
        proyectoServicioClave.setManagement(rs.getString("servicioclaveGerencia"));
        proyectoServicioClave.setBudgetItem(rs.getString("servicioclavePartida"));
        return proyectoServicioClave;
    }
}
