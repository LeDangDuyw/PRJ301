package dao;

import java.sql.*;

public class ReportDAO extends DBContext {

    public double getTotalRevenue() {
        String sql = "SELECT SUM(totalAmount) FROM Orders WHERE status='Completed'";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getOrderCount() {
        String sql = "SELECT COUNT(*) FROM Orders";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}