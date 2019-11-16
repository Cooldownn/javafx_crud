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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.Model.Course;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class TabCourseController implements Initializable
{
    @FXML TableView course_table;
    @FXML Button addBtn;
    @FXML TextField tf_name;
    @FXML TextField tf_code;
    @FXML TextArea tf_desc;
    @FXML Label nextID;
    private int index;


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

    public void checkField() {
        Alert alertError = new Alert(Alert.AlertType.ERROR);
        Alert alertSuccess = new Alert(Alert.AlertType.INFORMATION);
        String name = tf_name.getText();
        String code = tf_code.getText();
        String desc = tf_desc.getText();

        if (name.isEmpty()) {
            tf_name.requestFocus();
            alertError.setContentText("You cannot leave Course Name blank");
            alertError.show();
            return;
        }
        if (code.isEmpty()) {
            tf_code.requestFocus();
            alertError.setContentText("You cannot leave Course Code blank");
            alertError.show();
            return;
        }
        if (desc.isEmpty()) {
            tf_desc.requestFocus();
            alertError.setContentText("You cannot leave Course Description blank");
            alertError.show();
            return;
        }
        String sql = "INSERT INTO Course(course_name, course_code, course_desc) VALUES(?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, code);
            pstmt.setString(3, desc);
            pstmt.executeUpdate();
            alertSuccess.setContentText("Successfully create new course");
            alertSuccess.show();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Course> list = FXCollections.observableArrayList();

        TableColumn idCol = new TableColumn<Course, Integer>("ID");
        TableColumn nameCol = new TableColumn<Course, String>("Name");
        TableColumn codeCol = new TableColumn<Course, String>("Code");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));

        String sql = "SELECT id, course_name, course_code, course_desc FROM Course";
        try {
            Connection conn = this.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setCourseName(rs.getString("course_name"));
                course.setCourseCode(rs.getString("course_code"));
                index = rs.getInt("id");
                list.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        addBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                checkField();
                String name = tf_name.getText();
                String code = tf_code.getText();
                int mID = Integer.valueOf(nextID.getText());
                Course course = new Course();
                course.setId(mID);
                course.setCourseName(name);
                course.setCourseCode(code);
                list.add(course);
            }
        });
        course_table.setItems(list);
        course_table.getColumns().addAll(idCol, nameCol, codeCol);

        nextID.setText(String.valueOf(index + 1));

        course_table.setRowFactory(tv -> {
            TableRow<Course> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Course clickedRow = row.getItem();
                    printRow(clickedRow);
                }
            });
            return row;
        });
    }

    private void printRow(Course item) {
        System.out.println(item.getId());
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../resources/CourseDetail.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}