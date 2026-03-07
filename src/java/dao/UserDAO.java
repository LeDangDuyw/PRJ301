package dao;

import java.sql.*;
import model.User;

public class UserDAO extends DBContext {

    // LOGIN
    public User login(String username, String password) {

        String sql = "SELECT * FROM Users WHERE username=? AND password=? AND status=1";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // CHECK USERNAME EXIST
    public boolean isUsernameExist(String username) {

        String sql = "SELECT 1 FROM Users WHERE username=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username.trim());

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // CHECK EMAIL EXIST
    public boolean isEmailExist(String email) {

        String sql = "SELECT 1 FROM Users WHERE email=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email.trim());

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // REGISTER
    public boolean register(User user) {

        String sql = "INSERT INTO Users(username,password,fullname,email,phone,role,status) VALUES(?,?,?,?,?,?,1)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getUsername().trim());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullname());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setString(6, "user");

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // CHECK USER FOR RESET PASSWORD
    public User checkUserByUsernameAndEmail(String username, String email) {

        String sql = "SELECT * FROM Users WHERE username=? AND email=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, email.trim());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // UPDATE PASSWORD
    public void updatePassword(int id, String newPassword) {

        String sql = "UPDATE Users SET password=? WHERE id=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setInt(2, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CHANGE PASSWORD
    public boolean changePassword(int id, String oldPassword, String newPassword) {

        String sql = "SELECT id FROM Users WHERE id=? AND password=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, oldPassword);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                updatePassword(id, newPassword);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // UPDATE USER PROFILE
    public boolean updateUser(int id, String fullname, String email, String phone) {

        String sql = "UPDATE Users SET fullname=?, email=?, phone=? WHERE id=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fullname);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // EXTRACT USER
    private User extractUser(ResultSet rs) throws Exception {

        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("fullname"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("avatar"),
                rs.getString("role"),
                rs.getBoolean("status"),
                rs.getTimestamp("createdDate")
        );
    }
}