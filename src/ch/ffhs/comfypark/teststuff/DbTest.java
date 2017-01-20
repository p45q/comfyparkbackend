package ch.ffhs.comfypark.teststuff;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbTest {
    public static String test() {
        String databaseURL = "jdbc:mysql://comfyparkdb.cjn6mrex9bqq.eu-west-1.rds.amazonaws.com:3306/comfypark";
        String user = "ffhs";
        String password = "glauer.ch";
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(databaseURL, user, password);
            if (conn != null) {
                return "Connected to the database";
            }
        }
        catch (ClassNotFoundException ex) {
            return "Could not find database driver class";
        }
        catch (SQLException ex) {
            String string = ex.getMessage();
            return string;
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return "ok";
    }
}