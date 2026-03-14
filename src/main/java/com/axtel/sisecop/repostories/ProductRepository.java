package com.axtel.sisecop.repostories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.axtel.sisecop.dto.ProductDTO;
import com.axtel.sisecop.entities.ProyectoClaseProducto;
import com.axtel.sisecop.entities.ProyectoProducto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductRepository {

    private static final Logger log = LoggerFactory.getLogger(ProductRepository.class);

    public ProyectoProducto insertProduct(Connection conn, ProductDTO dto) throws SQLException {
        String sql = "INSERT INTO sisecop_serviciosproductos (servicioId, productoId, servicioproductoDescripcion) VALUES (?, ?, ?)";
        log.debug("Object: {}", "Executing: " + sql);
        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, dto.getServicioId());
            stmt.setInt(2, dto.getProductoId());
            stmt.setString(3, dto.getDescripcion());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating servicioProducto failed, no rows affected.");
            }
            log.trace("Object: {}", "Query executed. Affected Rows: " + affectedRows);
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    dto.setServicioproductoId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating servicioProducto failed, no ID obtained.");
                }
            }
            log.info("Object: {}", "Product saved succesfully!!! " + dto);
            return getProductoByServicioProductoId(conn, dto.getServicioproductoId());
        } catch (SQLException e) {
            throw new SQLException("Error inserting servicioProducto: " + e.getMessage(), e);
        }
    }

    public ProyectoProducto getProductoByServicioProductoId(Connection conn, int servicioProductoID) throws SQLException {
        ProyectoProducto producto = null;
        String sql = "SELECT sp.productoId, sp.servicioproductoId, p.productoNombre, p.claseId, c.claseNombre, sp.servicioproductoDescripcion " + " FROM sisecop_serviciosproductos sp " + "JOIN dbo.sisecop_productos p ON sp.productoId = p.productoId " + "JOIN dbo.sisecop_clases c ON p.claseId = c.claseId " + "WHERE sp.servicioproductoId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, servicioProductoID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    producto = mapToProyectoProducto(rs);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving product by servicioproductoId: " + e.getMessage(), e);
        }
        return producto;
    }

    public List<ProyectoProducto> readByServicioId(Connection conn, int servicioId) throws SQLException {
        List<ProyectoProducto> productos = new ArrayList<>();
        String sql = "SELECT sp.productoId, sp.servicioproductoId, p.productoNombre, p.claseId, c.claseNombre, sp.servicioproductoDescripcion " + " FROM sisecop_serviciosproductos sp " + " JOIN dbo.sisecop_productos p ON sp.productoId = p.productoId " + " JOIN dbo.sisecop_clases c ON p.claseId = c.claseId " + " WHERE sp.servicioId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, servicioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProyectoProducto producto = mapToProyectoProducto(rs);
                    productos.add(producto);
                }
            }
            return productos;
        } catch (SQLException e) {
            throw new SQLException("Error retrieving product by servicioproductoId: " + e.getMessage(), e);
        }
    }

    private ProyectoProducto mapToProyectoProducto(ResultSet rs) throws SQLException {
        ProyectoProducto producto = new ProyectoProducto();
        producto.setProductoId(rs.getInt("productoId"));
        producto.setProductoNombre(rs.getString("productoNombre"));
        producto.setProductoDescripcion(rs.getString("servicioproductoDescripcion"));
        producto.setServicioProductoID(rs.getInt("servicioproductoId"));
        ProyectoClaseProducto claseProducto = new ProyectoClaseProducto();
        claseProducto.setClaseId(rs.getInt("claseId"));
        claseProducto.setClaseNombre(rs.getString("claseNombre"));
        producto.setProductoClase(claseProducto);
        return producto;
    }

    public void deleteProduct(Connection connection, int servicioProductoId) throws SQLException {
        String sql = "DELETE FROM sisecop_serviciosproductos WHERE servicioproductoId = ?";
        log.debug("Object: {}", "Executing: " + sql);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, servicioProductoId);
            int affectedRows = stmt.executeUpdate();
            log.trace("Object: {}", "Query executed. Deleted Rows: " + affectedRows);
        } catch (SQLException e) {
            throw new SQLException("Error inserting servicioProducto: " + e.getMessage(), e);
        }
    }
}
