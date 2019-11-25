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
import sample.Model.Unit;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class TabUnitController implements Initializable
{
    @FXML TableView unit_table;
    @FXML TextField unit_name;
    @FXML TextField unit_code;
    @FXML ComboBox box_examiner;
    @FXML ComboBox box_lecturer;
    @FXML Button unitAddBtn;
    @FXML ComboBox unit_box;
    @FXML Button unitDelBtn;
    @FXML ComboBox box_code;
    @FXML Button mButton;

    private String offer = "";
    private int index;
    private int tableRow;
    private int delID;
    private String mCode = "";
    private String mExaminer = "";
    private String mLecturer = "";

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
        // Unit code only contains number
        unit_code.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    unit_code.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
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

        // Combo Box Code
        ObservableList<String> codeList = FXCollections.observableArrayList(
                "EEET","COSC","OENG","MIET","ISYS"
        );
        box_code.setItems(codeList);
        box_code.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mCode = box_code.getValue().toString();
            }
        });

        // Combo Box Lecturer and Examiner
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
                 mExaminer = box_examiner.getValue().toString();
            }
        });


        box_lecturer.setItems(staffList);
        box_lecturer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mLecturer = box_lecturer.getValue().toString();
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
                if (!isNumeric(code)) {
                    unit_code.requestFocus();
                    alertError.setContentText("Unit code should be numbers");
                    alertError.show();
                    return;
                }
                if (code.length() != 4) {
                    unit_code.requestFocus();
                    alertError.setContentText("Unit code must be 4 digit numbers");
                    alertError.show();
                    return;
                }
                if (mCode.isEmpty()) {
                    alertError.setContentText("Pleaes choose available unit code");
                    alertError.show();
                    return;
                }
                if (mExaminer.isEmpty()) {
                    alertError.setContentText("Please fill in Unit examiner");
                    alertError.show();
                    return;
                }
                if (mLecturer.isEmpty()) {
                    alertError.setContentText("Please fill in Unit lecturer");
                    alertError.show();
                    return;
                }

                if (offer.isEmpty()) {
                    return;
                }

                int check = Integer.valueOf(code.substring(0,1));
                if (check > 5) {
                    alertError.setContentText("First unit code number should be less than 5");
                    alertError.show();
                    return;
                }

                String sql = "INSERT INTO Unit(unit_name, unit_code, unit_examiner, unit_lecturer, unit_offer) VALUES(?,?,?,?,?)";

                try (Connection conn = TabUnitController.this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, name);
                    pstmt.setString(2, mCode + code);
                    pstmt.setString(3, mExaminer);
                    pstmt.setString(4,mLecturer);
                    pstmt.setString(5,offer);
                    int i = pstmt.executeUpdate();
                    if (i > 0) {
                        alertSuccess.setContentText("Successfully create new unit");
                        alertSuccess.show();
                        // Set table after adding
                        int mID = index + 1;
                        Unit unit = new Unit();
                        unit.setId(mID);
                        unit.setUnitName(name);
                        unit.setUnitCode(mCode + code);
                        list.add(unit);
                        index = mID + 1;

                        // Clear Content
                        unit_name.clear();
                        unit_code.clear();
                    } else {
                        System.out.println("Unit code has been taken");
                    }
                } catch (SQLException e) {
                    alertError.setContentText("Unit code has been taken");
                    alertError.show();
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

        mButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<Unit> list = tableData();
                unit_table.setItems(list);

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

    private static boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    ObservableList<Unit> tableData() {
        ObservableList<Unit> list = FXCollections.observableArrayList();
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
                list.add(unit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
