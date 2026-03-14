package com.axtel.sisecop.repostories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.axtel.sisecop.dto.ProjectPaymentDTO;
import com.axtel.sisecop.entities.ProyectoMes;
import com.axtel.sisecop.entities.ProyectoServicioPago;
import java.util.Base64;

public class ProjectPaymentRepository {

    private static final String INSERT_SQL = "INSERT INTO [dbo].[sisecop_serviciospagos] (servicioId, serviciopagoAnio, mesId, serviciopagoCantidad) VALUES (?, ?, ?, ?)";

    private static final String SELECT_BY_ID_SQL = "SELECT * FROM [dbo].[sisecop_serviciospagos] WHERE serviciopagoId = ?";

    private static final String SELECT_BY_SERVICE_ID_SQL = "SELECT * FROM [dbo].[sisecop_serviciospagos] WHERE servicioId = ?";

    private static final String SELECT_ALL_SQL = "SELECT * FROM [dbo].[sisecop_serviciospagos]";

    private static final String UPDATE_SQL = "UPDATE [dbo].[sisecop_serviciospagos] SET servicioId = ?, serviciopagoAnio = ?, mesId = ?, serviciopagoCantidad = ? WHERE serviciopagoId = ?";

    private static final String DELETE_SQL = "DELETE FROM [dbo].[sisecop_serviciospagos] WHERE serviciopagoId = ?";

    public ProyectoServicioPago create(Connection connection, ProjectPaymentDTO dto) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, dto.getIdService());
            preparedStatement.setInt(2, dto.getServicioPagoAnio());
            preparedStatement.setInt(3, dto.getMesPago().getMesId());
            preparedStatement.setBigDecimal(4, dto.getServicioPagoCantidad());
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    return selectProyectoServicioPagoById(connection, generatedId);
                } else
                    throw new SQLException("Se inserto el registro pero no fue posible recuperar el objeto por el ID");
            }
        }
    }

    public ProyectoServicioPago selectProyectoServicioPagoById(Connection connection, int id) throws SQLException {
        ProyectoServicioPago proyectoServicioPago = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                proyectoServicioPago = mapToProyectoServicioPago(rs);
            }
        }
        return proyectoServicioPago;
    }

    public List<ProyectoServicioPago> selectProyectoServicioPagoByServiceId(Connection connection, int serviceId) throws SQLException {
        List<ProyectoServicioPago> proyectoServicioPagoList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_SERVICE_ID_SQL)) {
            preparedStatement.setInt(1, serviceId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                proyectoServicioPagoList.add(mapToProyectoServicioPago(rs));
            }
        }
        return proyectoServicioPagoList;
    }

    public List<ProyectoServicioPago> selectAllProyectoServicioPago(Connection connection) throws SQLException {
        List<ProyectoServicioPago> proyectoServicioPagoList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SQL)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                proyectoServicioPagoList.add(mapToProyectoServicioPago(rs));
            }
        }
        return proyectoServicioPagoList;
    }

    public boolean updateProyectoServicioPago(Connection connection, ProjectPaymentDTO proyectoServicioPago) throws SQLException {
        boolean rowUpdated;
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setInt(1, proyectoServicioPago.getIdService());
            preparedStatement.setInt(2, proyectoServicioPago.getServicioPagoAnio());
            preparedStatement.setInt(3, proyectoServicioPago.getMesPago().getMesId());
            preparedStatement.setBigDecimal(4, proyectoServicioPago.getServicioPagoCantidad());
            preparedStatement.setInt(5, proyectoServicioPago.getServicioPagoId());
            rowUpdated = preparedStatement.executeUpdate() > 0;
        }
        return rowUpdated;
    }

    public boolean deleteProyectoServicioPago(Connection connection, int id) throws SQLException {
        boolean rowDeleted;
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, id);
            rowDeleted = preparedStatement.executeUpdate() > 0;
        }
        return rowDeleted;
    }

    private ProyectoServicioPago mapToProyectoServicioPago(ResultSet rs) throws SQLException {
        ProyectoServicioPago proyectoServicioPago = new ProyectoServicioPago();
        proyectoServicioPago.setServicioPagoId(rs.getInt("serviciopagoId"));
        proyectoServicioPago.setServicioPagoAnio(rs.getInt("serviciopagoAnio"));
        proyectoServicioPago.setMesPago(new ProyectoMes(rs.getInt("mesId")));
        proyectoServicioPago.setServicioPagoCantidad(rs.getBigDecimal("serviciopagoCantidad"));
        return proyectoServicioPago;
    }
}
