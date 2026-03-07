package dao;

import java.sql.*;
import model.OrderDetail;

public class OrderDetailDAO extends DBContext {

    public void insert(OrderDetail od) {
        String sql = "INSERT INTO OrderDetails(orderId,productId,quantity,price,discount) VALUES(?,?,?,?,?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, od.getOrderId());
            ps.setInt(2, od.getProductId());
            ps.setInt(3, od.getQuantity());
            ps.setDouble(4, od.getPrice());
            ps.setDouble(5, od.getDiscount());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}