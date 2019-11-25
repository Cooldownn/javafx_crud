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
    @FXML ComboBox box_director;
    @FXML ComboBox box_deputy;
    @FXML Label nextID;
    @FXML RadioButton radio_sgs;
    @FXML RadioButton radio_hn;
    @FXML ComboBox course_box;
    @FXML ComboBox box_code;
    @FXML Button mButton;

    private int index;
    private int delID;
    private int tableRow;
    private String radio = "";
    private String type = "";
    private String director = "";
    private String deputy = "";
    private String mCode = "";
    private String delCode = "";
    private int id = 0;
    private int currentIndex;
    private int nextIndex;

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
        nextID.setVisible(false);

        // Code only contains number
        tf_code.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    tf_code.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        // Radio Button
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

        // Combo Box Type
        ObservableList<String> mBox = FXCollections.observableArrayList(
                "UG","PG"
        );
        course_box.setItems(mBox);
        course_box.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                type = course_box.getValue().toString();
            }
        });

        // Combo Box Director and Deputy
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
                director = box_director.getValue().toString();
            }
        });

        // Combo Box Deputy
        box_deputy.setItems(staffList);
        box_deputy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deputy = box_deputy.getValue().toString();
            }
        });

        // Combo Box Code
        ObservableList<String> codeBox = FXCollections.observableArrayList(
                "BH","BP"
        );
        box_code.setItems(codeBox);
        box_code.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mCode = box_code.getValue().toString();
            }
        });

        // Table
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
                id = index + 1;
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

                if (type.isEmpty()) {
                    return;
                }

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

                if (code.length() != 3) {
                    tf_code.requestFocus();
                    alertError.setContentText("Course code must be 3 digit numbers");
                    alertError.show();
                    return;
                }

                if (mCode.isEmpty()) {
                    alertError.setContentText("Please choose available course code");
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

                currentIndex = index + 1;

                String sql = "INSERT INTO Course(id, course_name, course_code, course_desc, course_director, course_deputy, course_offer) VALUES(?,?,?,?,?,?,?)";

                try (Connection conn = TabCourseController.this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, id);
                    pstmt.setString(2, name);
                    pstmt.setString(3, mCode +code);
                    pstmt.setString(4, desc);
                    pstmt.setString(5,director);
                    pstmt.setString(6,deputy);
                    pstmt.setString(7, type);
                    int i = pstmt.executeUpdate();
                    if (i > 0) {
                        alertSuccess.setContentText("Successfully create new course");
                        alertSuccess.show();
                        Course course = new Course();
                        course.setId(id);
                        course.setCourseName(name);
                        course.setCourseCode(mCode + code);
                        course.setCourseDesc(radio);
                        list.add(course);

                        // Clear
                        tf_name.clear();
                        tf_code.clear();

                        // Increase ID
                        id++;
                        nextIndex = id;
                    } else {
                        System.out.println("Course code has been taken");
                    }
                } catch (SQLException e) {
                    alertError.setContentText("Course code has been taken");
                    alertError.show();
                    System.out.println(e.getMessage());
                }
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
                    delCode = clickedRow.getCourseCode();
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

                if (delID == index) {
                    id = delID;
                }

                if (delCode.isEmpty()) {
                    System.out.println("None");
                    return;
                } else {
                    String delSql = "DELETE FROM Course_Unit WHERE course_code = ?";
                    try (Connection conn = TabCourseController.this.connect();
                    PreparedStatement ps = conn.prepareStatement(delSql)) {
                        ps.setString(1, delCode);
                        ps.executeUpdate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (delID == index) {
                    id = delID;
                }
            }
        });

        mButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<Course> newList = tableData();
                    course_table.setItems(newList);
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

     ObservableList<Course> tableData() {
         ObservableList<Course> mList = FXCollections.observableArrayList();

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
                mList.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mList;
    }


}