package sample.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.Model.Staff;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;

public class TabStaffController implements Initializable {
    @FXML TableView staff_table;
//    private String currentDirectory = System.getProperty("user.dir");
//    private String path = currentDirectory + "/src/sample/staffs.txt";

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
        // Add Staff to DB at first time
//        try {
//            List<String> lines = Files.readAllLines(Paths.get(path));
//            String id;
//            String name;
//            String add;
//            for (int i = 1; i < lines.size(); i++){
//                String mLine = lines.get(i);
//                int index = mLine.indexOf(",");
//                id = mLine.substring(0,index);
//                String nextLine = lines.get(i);
//                name = nextLine.substring(nextLine.lastIndexOf(",") + 2);
//                i = i + 1;
//                add = lines.get(i);
//                insertDB(id, name, add);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // Table View
        ObservableList<Staff> list = FXCollections.observableArrayList();

        TableColumn idCol = new TableColumn<Staff, String>("ID");
        TableColumn nameCol = new TableColumn<Staff, String>("Name");
        TableColumn addressCol = new TableColumn<Staff, String>("Address");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("staffName"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("staffAddress"));

        String sql = "SELECT id, staff_name, staff_address FROM Staff";
        try {
            Connection conn = this.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Staff staff = new Staff();
                staff.setId(rs.getString("id"));
                staff.setStaffName(rs.getString("staff_name"));
                staff.setStaffAddress(rs.getString("staff_address"));
                list.add(staff);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        staff_table.setItems(list);
        staff_table.getColumns().addAll(idCol, nameCol, addressCol);

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
