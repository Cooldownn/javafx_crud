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
import javafx.stage.Stage;
import sample.Model.Unit;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class TabUnitController implements Initializable
{
    @FXML TableView unit_table;
    @FXML TextField unit_name;
    @FXML TextField unit_code;
    @FXML TextField unit_examiner;
    @FXML TextField unit_lecturer;
    @FXML Button unitAddBtn;
    @FXML ComboBox unit_box;
    @FXML Button unitDelBtn;
    @FXML ComboBox unit_course;

    private String offer = "";
    private int index;
    private int tableRow;
    private int delID;
    private String s1;
    private String chooseCourse;
    private String chooseOffer;

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
    public void initialize(URL location, ResourceBundle resources)
    {
        // Combo Box Offer
        ObservableList<String> mBox = FXCollections.observableArrayList(
                "S1","S2","S3","S1 & S2","S1 & S3","S2 & S3", "S1 & S2 & S3"
        );
        unit_box.setItems(mBox);
        unit_box.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                offer = unit_box.getValue().toString();
            }
        });

        // Combo Box Course
        ObservableList<String> mList = FXCollections.observableArrayList();
        String mSql = "SELECT course_code, course_offer FROM Course";
        try {
            Connection conn = this.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(mSql);

            while(rs.next()) {
                String courseCode = rs.getString("course_code");
                String courseOffer = rs.getString("course_offer");
                String combine = courseCode + " - " + courseOffer;
                mList.add(combine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        unit_course.setItems(mList);
        unit_course.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                s1 = unit_course.getValue().toString();
                chooseCourse = s1.substring(0,5);
                chooseOffer = s1.substring(8);
                System.out.println(chooseCourse);
            }
        });

        // Table View
        ObservableList<Unit> list = FXCollections.observableArrayList();

        TableColumn idCol = new TableColumn<Unit, Integer>("ID");
        TableColumn nameCol = new TableColumn<Unit, String>("Name");
        TableColumn codeCol = new TableColumn<Unit, String>("Code");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("unitName"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("unitCode"));

        String sql = "SELECT id, unit_name, unit_code FROM Unit";
        try {
            Connection conn = this.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Unit unit = new Unit();
                unit.setId(rs.getInt("id"));
                unit.setUnitName(rs.getString("unit_name"));
                unit.setUnitCode(rs.getString("unit_code"));
                index = rs.getInt("id");
                list.add(unit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        unit_table.setItems(list);
        unit_table.getColumns().addAll(idCol, nameCol, codeCol);

        unitAddBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alertError = new Alert(Alert.AlertType.ERROR);
                Alert alertSuccess = new Alert(Alert.AlertType.INFORMATION);

                String name = unit_name.getText();
                String code = unit_code.getText();
                String examiner = unit_examiner.getText();
                String lecturer = unit_lecturer.getText();

                if (name.isEmpty()) {
                    unit_name.requestFocus();
                    alertError.setContentText("Please fill in Unit name");
                    alertError.show();
                    return;
                }
                if (code.isEmpty()) {
                    unit_code.requestFocus();
                    alertError.setContentText("Please fill in Unit code");
                    alertError.show();
                    return;
                }
                if (examiner.isEmpty()) {
                    unit_examiner.requestFocus();
                    alertError.setContentText("Please fill in Unit examiner");
                    alertError.show();
                    return;
                }
                if (lecturer.isEmpty()) {
                    unit_lecturer.requestFocus();
                    alertError.setContentText("Please fill in Unit lecturer");
                    alertError.show();
                    return;
                }

                if (offer.isEmpty()) {
                    return;
                }

                String sql = "INSERT INTO Unit(unit_name, unit_code, unit_examiner, unit_lecturer, unit_offer) VALUES(?,?,?,?,?)";

                try (Connection conn = TabUnitController.this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, name);
                    pstmt.setString(2, code);
                    pstmt.setString(3, examiner);
                    pstmt.setString(4,lecturer);
                    pstmt.setString(5,offer);
                    pstmt.executeUpdate();
                    alertSuccess.setContentText("Successfully create new unit");
                    alertSuccess.show();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

                // Set table after adding
                int mID = index + 1;
                Unit unit = new Unit();
                unit.setId(mID);
                unit.setUnitName(name);
                unit.setUnitCode(code);
                list.add(unit);

                // Clear Content
                unit_name.clear();
                unit_code.clear();
                unit_lecturer.clear();
                unit_examiner.clear();

                // If choose to add course
                // Check Level 1,2,3 to UG and 4,5 to PG
                int inputCode = Integer.valueOf(code.substring(4,5));
                if (inputCode < 4 && chooseOffer.equals("PG")) {
                    alertError.setContentText("Cannot add to course");
                    alertError.show();
                    return;
                }
                if (inputCode > 3 && chooseOffer.equals("UG")) {
                    alertError.setContentText("Cannot add to course");
                    alertError.show();
                    return;
                }
                String mSql = "INSERT INTO Course_Unit(course_code, unit_code) VALUES(?,?)";
                try (Connection conn = TabUnitController.this.connect();
                     PreparedStatement st = conn.prepareStatement(mSql)) {
                    st.setString(1,chooseCourse);
                    st.setString(2,code);
                    st.executeUpdate();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        unit_table.setRowFactory(tv -> {
            TableRow<Unit> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Unit clickedRow = row.getItem();
                    printRow(clickedRow);
                }
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    Unit clickedRow = row.getItem();
                    tableRow = row.getIndex();
                    delID = clickedRow.getId();
                }
            });
            return row;
        });

        unitDelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (delID == 0) {
                    System.out.println("Nothing happens");
                    return;
                }
                String sql = "DELETE FROM Unit WHERE id = ?";

                try (Connection conn = TabUnitController.this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    // set the corresponding param
                    pstmt.setInt(1, delID);
                    // execute the delete statement
                    pstmt.executeUpdate();

                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

                unit_table.getItems().remove(tableRow);
            }
        });
    }

    private void printRow(Unit item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/UnitDetail.fxml"));
            Parent root = loader.load();
            UnitDetailController controller = loader.getController();
            controller.getData(item.getId());
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
