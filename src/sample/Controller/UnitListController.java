package sample.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.Model.Unit;

import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;

public class UnitListController implements Initializable {
    @FXML TableView unitlist_table;

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

    }

    public void getID(String courseCode) {
        ObservableList<Unit> list = FXCollections.observableArrayList();

        TableColumn nameCol = new TableColumn<Unit, String>("Name");
        TableColumn codeCol = new TableColumn<Unit, String>("Code");
        TableColumn examCol = new TableColumn<Unit, String>("Examiner");
        TableColumn lecCol = new TableColumn<Unit, String>("Lecturer");
        TableColumn offerCol = new TableColumn<Unit, String>("Semester Offer");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("unitName"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("unitCode"));
        examCol.setCellValueFactory(new PropertyValueFactory<>("unitExam"));
        lecCol.setCellValueFactory(new PropertyValueFactory<>("unitLec"));
        offerCol.setCellValueFactory(new PropertyValueFactory<>("unitOffer"));

        String sql = "SELECT unit_name, Unit.unit_code, unit_examiner, unit_lecturer, unit_offer FROM Unit INNER JOIN Course_Unit ON Unit.unit_code = Course_Unit.unit_code WHERE (course_code = ? AND course_code IS NOT NULL)";

        try {
            Connection conn = this.connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Unit unit = new Unit();
                unit.setUnitName(rs.getString("unit_name"));
                unit.setUnitCode(rs.getString("unit_code"));
                unit.setUnitExam(rs.getString("unit_examiner"));
                unit.setUnitLec(rs.getString("unit_lecturer"));
                unit.setUnitOffer(rs.getString("unit_offer"));
                list.add(unit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        unitlist_table.setItems(list);
        unitlist_table.getColumns().addAll(nameCol,codeCol,examCol,lecCol, offerCol);
    }
}
