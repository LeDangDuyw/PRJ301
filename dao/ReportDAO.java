/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import utils.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Personal
 */
public class ReportDAO {
     public double getTotalRevenue() {
        String sql = "SELECT ISNULL(SUM(totalAmount), 0) AS totalRevenue FROM Orders";
        return getSingleDouble(sql, "totalRevenue");
    }

    public int getOrderCount() {
        String sql = "SELECT COUNT(*) AS orderCount FROM Orders";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("orderCount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getMaxPrice() {
        String sql = "SELECT ISNULL(MAX(price), 0) AS maxPrice FROM OrderDetails";
        return getSingleDouble(sql, "maxPrice");
    }

    public double getMinPrice() {
        String sql = "SELECT ISNULL(MIN(price), 0) AS minPrice FROM OrderDetails";
        return getSingleDouble(sql, "minPrice");
    }

    public double getAvgPrice() {
        String sql = "SELECT ISNULL(AVG(CAST(price AS FLOAT)), 0) AS avgPrice FROM OrderDetails";
        return getSingleDouble(sql, "avgPrice");
    }

    private double getSingleDouble(String sql, String columnName) {
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(columnName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
