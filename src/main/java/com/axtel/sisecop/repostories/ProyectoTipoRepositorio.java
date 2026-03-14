package com.axtel.sisecop.repostories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.axtel.sisecop.entities.ProyectoTipo;
import java.util.Base64;

public class ProyectoTipoRepositorio {

    public ProyectoTipo findById(Connection connection, int id) throws SQLException {
        String sql = "SELECT * FROM sisecop_tipos WHERE tipoId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToProyectoTipo(resultSet);
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return null;
    }

    public boolean insert(Connection connection, ProyectoTipo tipo) throws SQLException {
        String sql = "INSERT INTO sisecop_tipos (tipoNombre) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, tipo.getTipoProyectoNombre());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        tipo.setTipoProyectoId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            throw e;
        }
    }

    public boolean update(Connection connection, ProyectoTipo tipo) throws SQLException {
        String sql = "UPDATE sisecop_tipos SET tipoNombre = ? WHERE tipoId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tipo.getTipoProyectoNombre());
            statement.setInt(2, tipo.getTipoProyectoId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    public boolean delete(Connection connection, int id) throws SQLException {
        String sql = "DELETE FROM sisecop_tipos WHERE tipoId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    private ProyectoTipo mapToProyectoTipo(ResultSet resultSet) throws SQLException {
        ProyectoTipo tipo = new ProyectoTipo();
        tipo.setTipoProyectoId(resultSet.getInt("tipoId"));
        tipo.setTipoProyectoNombre(resultSet.getString("tipoNombre"));
        return tipo;
    }
}
