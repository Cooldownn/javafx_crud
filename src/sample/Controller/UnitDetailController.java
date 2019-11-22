package sample.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class UnitDetailController implements Initializable {
    @FXML TextField unit_detailID;
    @FXML TextField unit_detailName;
    @FXML TextField unit_detailCode;
    @FXML TextField unit_detailExaminer;
    @FXML TextField unit_detailLecturer;
    @FXML TextField unit_detailOffer;
    @FXML Button unit_detailBtn;
    @FXML ListView listView;
    @FXML ComboBox newCourseBox;
    @FXML Button assignNewCourse;
    @FXML ComboBox box_examiner;
    @FXML ComboBox box_lecturer;

    private String current = "";
    private String userChoose = "";
    private String isValid = "";
    private String code = "";
    private ObservableList<String> courseList = FXCollections.observableArrayList();

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
        Alert alertError = new Alert(Alert.AlertType.ERROR);
        Alert alertSuccess = new Alert(Alert.AlertType.INFORMATION);
        unit_detailExaminer.setEditable(false);
        unit_detailLecturer.setEditable(false);
        unit_detailID.setEditable(false);
        unit_detailCode.setEditable(false);

        // Combo Box New Course
        ObservableList<String> mList = FXCollections.observableArrayList();
        String mSql = "SELECT course_code, course_offer FROM Course";
        try {
            Connection conn = this.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(mSql);

            while(rs.next()) {
                String courseCode = rs.getString("course_code");
                String courseOffer = rs.getString("course_offer");
                mList.add(courseCode + " - " +courseOffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        newCourseBox.setItems(mList);
        newCourseBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String opt = newCourseBox.getValue().toString();
                userChoose = opt.substring(0,5);
                isValid = opt.substring(8,10);
            }
        });

        //
        assignNewCourse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (userChoose.equals(current)) {
                    alertError.setContentText("This course has already been assigned");
                    alertError.show();
                    return;
                }
                int check = Integer.valueOf(code.substring(4,5));
                if (check < 4 && isValid.equals("PG")) {
                    alertError.setContentText("UG Unit cannot add to PG Course");
                    alertError.show();
                    return;
                }
                if (check > 3 && isValid.equals("UG")) {
                    alertError.setContentText("PG Unit cannot add to UG Course");
                    alertError.show();
                    return;
                }

                String mSQL = "INSERT INTO Course_Unit(course_code, unit_code) VALUES(?,?)";
                try (Connection conn = UnitDetailController.this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(mSQL)) {
                    pstmt.setString(1, userChoose);
                    pstmt.setString(2, code);
                    pstmt.executeUpdate();
                    alertSuccess.setContentText("Successfully add to course " + userChoose);
                    alertSuccess.show();
                    listView.getItems().add(userChoose);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        unit_detailBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Integer id = Integer.valueOf(unit_detailID.getText());
                String name = unit_detailName.getText();
                String code = unit_detailCode.getText();
                String exam = unit_detailExaminer.getText();
                String lec = unit_detailLecturer.getText();
                String offer = unit_detailOffer.getText();

                if (name.isEmpty()) {
                    unit_detailName.requestFocus();
                    return;
                }
                if (code.isEmpty()) {
                    unit_detailCode.requestFocus();
                    return;
                }
                if (exam.isEmpty()) {
                    unit_detailExaminer.requestFocus();
                    return;
                }
                if (lec.isEmpty()) {
                    unit_detailLecturer.requestFocus();
                    return;
                }
                if (offer.isEmpty()) {
                    unit_detailOffer.requestFocus();
                    return;
                }

                String sql = "UPDATE Unit SET unit_name = ? , "
                        + "unit_code = ? , "
                        + "unit_examiner = ? , "
                        + "unit_lecturer = ? , "
                        + "unit_offer = ? "
                        + "WHERE id = ?";

                try (Connection conn = UnitDetailController.this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    // set the corresponding param
                    pstmt.setString(1, name);
                    pstmt.setString(2, code);
                    pstmt.setString(3, exam);
                    pstmt.setString(4, lec);
                    pstmt.setString(5, offer);
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

        // Combo Box Examiner
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

        box_examiner.setItems(staffList);
        box_examiner.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String examiner = box_examiner.getValue().toString();
                unit_detailExaminer.setText(examiner);
            }
        });

        box_lecturer.setItems(staffList);
        box_lecturer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String lecturer = box_lecturer.getValue().toString();
                unit_detailLecturer.setText(lecturer);
            }
        });

    }

    public void getData(int id) {
        String sql = "SELECT * FROM Unit WHERE id = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, id);
            // execute the delete statement
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                unit_detailID.setText(String.valueOf(rs.getInt("id")));
                unit_detailName.setText(rs.getString("unit_name"));
                unit_detailCode.setText(rs.getString("unit_code"));
                unit_detailExaminer.setText(rs.getString("unit_examiner"));
                unit_detailLecturer.setText(rs.getString("unit_lecturer"));
                unit_detailOffer.setText(rs.getString("unit_offer"));
                code = rs.getString("unit_code");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Get Current Course
        String sql1 = "SELECT course_code FROM Course_Unit WHERE (unit_code = ? AND course_code IS NOT NULL)";
        try (Connection conn = UnitDetailController.this.connect();
             PreparedStatement ps = conn.prepareStatement(sql1)) {

            ps.setString(1, unit_detailCode.getText());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                current = rs.getString("course_code");
                courseList.add(current);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        listView.setOrientation(Orientation.VERTICAL);
        listView.getItems().addAll(courseList);
    }
}
