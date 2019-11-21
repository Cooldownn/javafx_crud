package sample.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class CourseDetailController implements Initializable {
    @FXML Button detailSaveBtn;
    @FXML TextField detail_id;
    @FXML TextField detail_name;
    @FXML TextField detail_code;
    @FXML TextField detail_desc;
    @FXML TextField detail_director;
    @FXML TextField detail_deputy;
    @FXML TextField detail_offer;
    @FXML Button unitBtn;
    @FXML ComboBox box_director;
    @FXML ComboBox box_deputy;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        detail_id.setEditable(false);
        detail_desc.setEditable(false);
        detail_offer.setEditable(false);
        detail_director.setEditable(false);
        detail_deputy.setEditable(false);
        Alert alertSuccess = new Alert(Alert.AlertType.INFORMATION);
        detailSaveBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Integer id = Integer.valueOf(detail_id.getText());
                String name = detail_name.getText();
                String code = detail_code.getText();
                String desc = detail_desc.getText();
                String director = detail_director.getText();
                String deputy = detail_deputy.getText();
                if (name.isEmpty()) {
                    detail_name.requestFocus();
                    return;
                }
                if (code.isEmpty()) {
                    detail_code.requestFocus();
                    return;
                }
                if (desc.isEmpty()) {
                    detail_desc.requestFocus();
                    return;
                }
                if (director.isEmpty()) {
                    detail_director.requestFocus();
                    return;
                }
                if (deputy.isEmpty()) {
                    detail_deputy.requestFocus();
                    return;
                }
                String sql = "UPDATE Course SET course_name = ? , "
                        + "course_code = ? , "
                        + "course_desc = ? , "
                        + "course_director = ? , "
                        + "course_deputy = ? "
                        + "WHERE id = ?";

                try (Connection conn = CourseDetailController.this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    // set the corresponding param
                    pstmt.setString(1, name);
                    pstmt.setString(2, code);
                    pstmt.setString(3, desc);
                    pstmt.setString(4, director);
                    pstmt.setString(5, deputy);
                    pstmt.setInt(6, id);
                    // update
                    pstmt.executeUpdate();
                    alertSuccess.setContentText("Successfully update");
                    alertSuccess.show();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        unitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/UnitList.fxml"));
                    Parent root = loader.load();
                    UnitListController controller = loader.getController();
                    controller.getID(detail_code.getText());
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setTitle("List of available units in course: " + detail_code.getText());
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        // Combo Box Course Director
        ObservableList<String> staffList = FXCollections.observableArrayList();
        String sql1 = "SELECT staff_name FROM Staff";
        try {
            Connection conn = this.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql1);

            while (rs.next()) {
                String staffName = rs.getString("staff_name");
                staffList.add(staffName);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        box_director.setItems(staffList);
        box_director.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String director = box_director.getValue().toString();
                detail_director.setText(director);
            }
        });

        // Combo Box Deputy
        box_deputy.setItems(staffList);
        box_deputy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String deputy = box_deputy.getValue().toString();
                detail_deputy.setText(deputy);
            }
        });
    }

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

    public void myFunction(int id) {
        String sql = "SELECT * FROM Course WHERE id = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, id);
            // execute the delete statement
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                detail_id.setText(String.valueOf(rs.getInt("id")));
                detail_name.setText(rs.getString("course_name"));
                detail_code.setText(rs.getString("course_code"));
                detail_desc.setText(rs.getString("course_desc"));
                detail_director.setText(rs.getString("course_director"));
                detail_deputy.setText(rs.getString("course_deputy"));
                detail_offer.setText(rs.getString("course_offer"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
