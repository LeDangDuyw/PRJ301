package dao;

import java.sql.*;
import model.Order;

public class OrderDAO extends DBContext {

    public int insertOrder(Order order) {
        String sql = "INSERT INTO Orders(userId,couponId,discountAmount,totalAmount,shippingAddress,phoneReceiver,receiverName,paymentMethod,status) "
                + "VALUES(?,?,?,?,?,?,?,?,?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, order.getUserId());
            ps.setObject(2, order.getCouponId());
            ps.setDouble(3, order.getDiscountAmount());
            ps.setDouble(4, order.getTotalAmount());
            ps.setString(5, order.getShippingAddress());
            ps.setString(6, order.getPhoneReceiver());
            ps.setString(7, order.getReceiverName());
            ps.setString(8, order.getPaymentMethod());
            ps.setString(9, order.getStatus());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}