package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBContext {

    private final String URL = "jdbc:sqlserver://localhost:1433;databaseName=HomeElectroDB;encrypt=true;trustServerCertificate=true;";
    private final String USER = "sa";        
    private final String PASSWORD = "123"; 

    public Connection getConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}