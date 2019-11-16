package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable
{

    @FXML
    Tab tabCourse;

    @FXML
    Tab tabUnit;

    @FXML
    Tab tabStaff;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        FXMLLoader loader = new FXMLLoader();
        try
        {
            AnchorPane anch1 = loader.load(getClass().getResource("resources/TabCourse.fxml"));
            tabCourse.setContent(anch1);
        }
        catch(IOException e)
        {
            System.out.println("File not found");
        }
        loader = new FXMLLoader();
        try
        {
            AnchorPane anch2 = loader.load(getClass().getResource("resources/TabUnit.fxml"));
            tabUnit.setContent(anch2);
        }
        catch(IOException e)
        {
            System.out.println("File not found");
        }

        loader = new FXMLLoader();
        try {
            AnchorPane anch3 = loader.load(getClass().getResource("resources/TabStaff.fxml"));
            tabStaff.setContent(anch3);
        } catch (IOException e) {
            System.out.println("File not found");
        }

    }
}