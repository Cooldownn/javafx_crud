package sample.Controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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
        unit_detailID.setEditable(false);

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
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
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
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
