import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

public class Gui extends Application {

    public static void main(String[] args) {
        launch();

    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        primaryStage.setTitle("Search Engine");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    private Text Title;
    @FXML
    private TextField search_bar;

    @FXML
    private Button search_button;

    @FXML
    void search(ActionEvent event) {
        String query= search_bar.getText();
        Title.setText(query);
    }
}
