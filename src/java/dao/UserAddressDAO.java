package dao;

import java.sql.*;
import java.util.*;
import model.UserAddress;

public class UserAddressDAO extends DBContext {

    public List<UserAddress> getByUser(int userId) {
        List<UserAddress> list = new ArrayList<>();
        String sql = "SELECT * FROM UserAddresses WHERE userId=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new UserAddress(
                        rs.getInt("id"),
                        rs.getInt("userId"),
                        rs.getString("fullAddress"),
                        rs.getString("receiverName"),
                        rs.getString("receiverPhone"),
                        rs.getBoolean("isDefault")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}