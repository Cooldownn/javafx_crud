package sample.Controller;

import javafx.fxml.Initializable;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class TabStaffController implements Initializable {
    private String currentDirectory = System.getProperty("user.dir");
    private String path = currentDirectory + "/src/sample/staffs.txt";

    private Connection connect() {
        // SQLite connection string
        String currentDirectory = System.getProperty("user.dir");
        String path = currentDirectory + "/src/sample/Service/database.db";
        String url = "jdbc:sqlite:" + path;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            String id;
            String name;
            String add;
            for (int i = 1; i < lines.size(); i++){
                String mLine = lines.get(i);
                int index = mLine.indexOf(",");
                id = mLine.substring(0,index);
                String nextLine = lines.get(i);
                name = nextLine.substring(nextLine.lastIndexOf(",") + 2);
                i = i + 1;
                add = lines.get(i);
//                insertDB(id, name, add);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertDB(String id, String name, String address) {
        String sql = "INSERT INTO Staff(id, staff_name, staff_address) VALUES (?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, address);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
