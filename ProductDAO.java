package dao;

import java.sql.*;
import java.util.*;
import model.Product;

public class ProductDAO extends DBContext {

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE status=1";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(extractProduct(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public Product getById(int id) {
        String sql = "SELECT * FROM Products WHERE id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractProduct(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // ── BỔ SUNG: đếm tổng sản phẩm đang hiển thị ─────────────────────────
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM Products WHERE status=1";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    // ──────────────────────────────────────────────────────────────────────

    private Product extractProduct(ResultSet rs) throws Exception {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("slug"),
                rs.getString("description"),
                rs.getDouble("price"),
                rs.getInt("stock"),
                rs.getInt("sold"),
                rs.getString("image"),
                rs.getDouble("discount"),
                rs.getInt("warranty"),
                rs.getBoolean("isFeatured"),
                rs.getBoolean("status"),
                rs.getTimestamp("createdDate"),
                rs.getInt("categoryId"),
                rs.getInt("brandId")
        );
    }

    public boolean delete(int id) {
        String sql = "UPDATE Products SET status = 0 WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean insert(Product p) {
        String sql = "INSERT INTO Products "
                + "(name, slug, description, price, stock, sold, image, "
                + "discount, warranty, isFeatured, status, createdDate, "
                + "categoryId, brandId) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getSlug());
            ps.setString(3, p.getDescription());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getStock());
            ps.setInt(6, p.getSold());
            ps.setString(7, p.getImage());
            ps.setDouble(8, p.getDiscount());
            ps.setInt(9, p.getWarranty());
            ps.setBoolean(10, p.isIsFeatured());
            ps.setBoolean(11, p.isStatus());
            ps.setTimestamp(12, new Timestamp(p.getCreatedDate().getTime()));
            ps.setInt(13, p.getCategoryId());
            ps.setInt(14, p.getBrandId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(Product p) {
        String sql = "UPDATE Products SET "
                + "name=?, slug=?, description=?, price=?, stock=?, "
                + "image=?, discount=?, warranty=?, isFeatured=?, "
                + "status=?, categoryId=?, brandId=? "
                + "WHERE id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getSlug());
            ps.setString(3, p.getDescription());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getStock());
            ps.setString(6, p.getImage());
            ps.setDouble(7, p.getDiscount());
            ps.setInt(8, p.getWarranty());
            ps.setBoolean(9, p.isIsFeatured());
            ps.setBoolean(10, p.isStatus());
            ps.setInt(11, p.getCategoryId());
            ps.setInt(12, p.getBrandId());
            ps.setInt(13, p.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
