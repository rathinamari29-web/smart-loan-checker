package db;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:loan_history.db";

    static {
        try {
            // Explicitly load SQLite JDBC Driver
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS loan_applications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "applicant_name TEXT NOT NULL, " +
                "age INTEGER, " +
                "income REAL, " +
                "credit_score INTEGER, " +
                "existing_emi REAL, " +
                "loan_type TEXT, " +
                "loan_amount REAL, " +
                "tenure INTEGER, " +
                "result TEXT, " +
                "recommended_lender TEXT, " +
                "emi REAL, " +
                "application_date TEXT" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("SQLite Database initialized: loan_history.db");
        } catch (SQLException e) {
            System.err.println("Error initializing SQLite Database: " + e.getMessage());
        }
    }

    public static boolean saveApplicationRecord(String applicantName, int age, double income, int creditScore, 
                                               double existingEmi, String loanType, double loanAmount, int tenure, 
                                               String resultStatus, String recommendedLender, double emi) {
        
        String insertSQL = "INSERT INTO loan_applications " +
                "(applicant_name, age, income, credit_score, existing_emi, loan_type, loan_amount, tenure, result, recommended_lender, emi, application_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, applicantName);
            pstmt.setInt(2, age);
            pstmt.setDouble(3, income);
            pstmt.setInt(4, creditScore);
            pstmt.setDouble(5, existingEmi);
            pstmt.setString(6, loanType);
            pstmt.setDouble(7, loanAmount);
            pstmt.setInt(8, tenure);
            pstmt.setString(9, resultStatus);
            pstmt.setString(10, recommendedLender);
            pstmt.setDouble(11, emi);
            pstmt.setString(12, timestamp);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving application to SQLite DB: " + e.getMessage());
            return false;
        }
    }

    public static List<Map<String, Object>> getApplicationHistory() {
        List<Map<String, Object>> historyList = new ArrayList<>();
        String querySQL = "SELECT * FROM loan_applications ORDER BY id DESC;";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("applicant_name", rs.getString("applicant_name"));
                row.put("age", rs.getInt("age"));
                row.put("income", rs.getDouble("income"));
                row.put("credit_score", rs.getInt("credit_score"));
                row.put("existing_emi", rs.getDouble("existing_emi"));
                row.put("loan_type", rs.getString("loan_type"));
                row.put("loan_amount", rs.getDouble("loan_amount"));
                row.put("tenure", rs.getInt("tenure"));
                row.put("result", rs.getString("result"));
                row.put("recommended_lender", rs.getString("recommended_lender"));
                row.put("emi", rs.getDouble("emi"));
                row.put("application_date", rs.getString("application_date"));

                historyList.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching history from SQLite DB: " + e.getMessage());
        }
        return historyList;
    }

    public static boolean clearHistory() {
        String deleteSQL = "DELETE FROM loan_applications;";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteSQL);
            return true;
        } catch (SQLException e) {
            System.err.println("Error clearing SQLite history: " + e.getMessage());
            return false;
        }
    }
}
