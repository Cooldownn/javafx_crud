package sample.Controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    @FXML Button delBtn;
    @FXML TextField tf_name;
    @FXML TextField tf_code;
    @FXML TextField tf_director;
    @FXML TextField tf_deputy;
    @FXML Label nextID;
    @FXML RadioButton radio_sgs;
    @FXML RadioButton radio_hn;
    private int index;
    private int delID;
    private int tableRow;
    private String radio = "";

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
        ToggleGroup tg = new ToggleGroup();
        radio_sgs.setToggleGroup(tg);
        radio_hn.setToggleGroup(tg);

        tg.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                RadioButton rb = (RadioButton)tg.getSelectedToggle();
                if (rb != null) {
                    radio = rb.getText();
                }
            }
        });
        ObservableList<Course> list = FXCollections.observableArrayList();

        TableColumn idCol = new TableColumn<Course, Integer>("ID");
        TableColumn nameCol = new TableColumn<Course, String>("Name");
        TableColumn codeCol = new TableColumn<Course, String>("Code");
        TableColumn locCol = new TableColumn<Course, String>("Location");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        locCol.setCellValueFactory(new PropertyValueFactory<>("courseDesc"));

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
                course.setCourseDesc(rs.getString("course_desc"));
                index = rs.getInt("id");
                list.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        addBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alertError = new Alert(Alert.AlertType.ERROR);
                Alert alertSuccess = new Alert(Alert.AlertType.INFORMATION);

                String name = tf_name.getText();
                String code = tf_code.getText();
                String desc = radio;
                String director = tf_director.getText();
                String deputy = tf_deputy.getText();

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
                    alertError.setContentText("Please choose location");
                    alertError.show();
                    return;
                }

                if (director.isEmpty()) {
                    alertError.setContentText("You cannot leave Course Director blank");
                    alertError.show();
                    return;
                }

                if (deputy.isEmpty()) {
                    alertError.setContentText("You cannot leave Course Deputy blank");
                    alertError.show();
                    return;
                }

                String sql = "INSERT INTO Course(course_name, course_code, course_desc, course_director, course_deputy) VALUES(?,?,?,?,?)";

                try (Connection conn = TabCourseController.this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, name);
                    pstmt.setString(2, code);
                    pstmt.setString(3, desc);
                    pstmt.setString(4,director);
                    pstmt.setString(5,deputy);
                    pstmt.executeUpdate();
                    alertSuccess.setContentText("Successfully create new course");
                    alertSuccess.show();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

                int mID = Integer.valueOf(index + 1);
                Course course = new Course();
                course.setId(mID);
                course.setCourseName(name);
                course.setCourseCode(code);
                course.setCourseDesc(radio);
                list.add(course);

                // Clear
                mID = mID + 1;
                nextID.setText(String.valueOf(mID));
                tf_name.clear();
                tf_code.clear();
            }
        });
        course_table.setItems(list);
        course_table.getColumns().addAll(idCol, nameCol, codeCol, locCol);

        nextID.setText(String.valueOf(index + 1));

        course_table.setRowFactory(tv -> {
            TableRow<Course> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Course clickedRow = row.getItem();
                    printRow(clickedRow);
                }
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    Course clickedRow = row.getItem();
                    tableRow = row.getIndex();
                    delID = clickedRow.getId();
                }
            });
            return row;
        });

        delBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (delID == 0) {
                    System.out.println("Nothing happens");
                    return;
                }

                String sql = "DELETE FROM Course WHERE id = ?";

                try (Connection conn = TabCourseController.this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    // set the corresponding param
                    pstmt.setInt(1, delID);
                    // execute the delete statement
                    pstmt.executeUpdate();

                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

                course_table.getItems().remove(tableRow);
            }
        });

    }

    private void printRow(Course item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/CourseDetail.fxml"));
            Parent root = loader.load();
            CourseDetailController controller = loader.getController();
            controller.myFunction(item.getId());
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}