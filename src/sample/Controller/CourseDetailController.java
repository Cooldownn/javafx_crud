package sample.Controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        detail_id.setEditable(false);
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
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
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
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
