package jpractice.chat;/**
 *
 * @author Alexander Vlasov
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Gui extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("chat.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();

    }
}
