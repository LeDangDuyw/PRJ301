/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import Model.Order;
import utils.DBContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Personal
 */
public class OrderDAO {
     public int insertOrder(Order order) {
        String sql = "INSERT INTO Orders (userId, totalAmount, shippingAddress, phoneReceiver, receiverName, paymentMethod, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, order.getUserId());
            ps.setDouble(2, order.getTotalAmount());
            ps.setString(3, order.getShippingAddress());
            ps.setString(4, order.getPhoneReceiver());
            ps.setString(5, order.getReceiverName());
            ps.setString(6, order.getPaymentMethod());
            ps.setString(7, order.getStatus());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Order> getOrdersByUser(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE userId = ? ORDER BY id DESC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Order o = new Order();
                o.setId(rs.getInt("id"));
                o.setUserId(rs.getInt("userId"));
                o.setTotalAmount(rs.getDouble("totalAmount"));
                o.setShippingAddress(rs.getString("shippingAddress"));
                o.setPhoneReceiver(rs.getString("phoneReceiver"));
                o.setReceiverName(rs.getString("receiverName"));
                o.setPaymentMethod(rs.getString("paymentMethod"));
                o.setStatus(rs.getString("status"));
                try {
                    o.setCreatedAt(rs.getTimestamp("createdAt"));
                } catch (Exception ignore) {
                }
                list.add(o);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders ORDER BY id DESC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order o = new Order();
                o.setId(rs.getInt("id"));
                o.setUserId(rs.getInt("userId"));
                o.setTotalAmount(rs.getDouble("totalAmount"));
                o.setShippingAddress(rs.getString("shippingAddress"));
                o.setPhoneReceiver(rs.getString("phoneReceiver"));
                o.setReceiverName(rs.getString("receiverName"));
                o.setPaymentMethod(rs.getString("paymentMethod"));
                o.setStatus(rs.getString("status"));
                try {
                    o.setCreatedAt(rs.getTimestamp("createdAt"));
                } catch (Exception ignore) {
                }
                list.add(o);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
