//package sample.Service;
//
//import java.sql.*;
//
//public class SQLite {
//
//    private Connection connect() {
//        // SQLite connection string
//        String currentDirectory = System.getProperty("user.dir");
//        String path = currentDirectory + "/src/sample/Service/database.db";
//        String url = "jdbc:sqlite:" + path;
//        Connection conn = null;
//        try {
//            conn = DriverManager.getConnection(url);
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//        return conn;
//    }
//
// Read Data
//    public void query() {
//        String sql = "SELECT id, course_name, course_code, course_desc FROM Course";
//        try {
//            Connection conn = this.connect();
//            Statement st = conn.createStatement();
//            ResultSet rs = st.executeQuery(sql);
//
//            while (rs.next()) {
//                System.out.println(rs.getInt("id") + "\t" +
//                        rs.getString("course_name") + "\t" +
//                        rs.getString("course_code") + "\t" +
//                        rs.getString("course_desc"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

// Insert Data
//    public void insert(String name, String code, String desc) {
//        String sql = "INSERT INTO Course(course_name, course_code, course_desc) VALUES(?,?,?)";
//
//        try (Connection conn = this.connect();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, name);
//            pstmt.setString(2, code);
//            pstmt.setString(3, desc);
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//    }
