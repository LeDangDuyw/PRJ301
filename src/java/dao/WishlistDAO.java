package dao;

import java.sql.*;

public class WishlistDAO extends DBContext {

    public void add(int userId, int productId) {
        String sql = "INSERT INTO Wishlist(userId,productId) VALUES(?,?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove(int userId, int productId) {
        String sql = "DELETE FROM Wishlist WHERE userId=? AND productId=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}