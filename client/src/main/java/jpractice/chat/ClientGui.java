package jpractice.chat;/**
 *
 * @author Alexander Vlasov
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientGui extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage clientGui) throws IOException {


        Parent authorizationParent = FXMLLoader.load(ClassLoader.getSystemClassLoader().getResource("authorization.fxml"));
        Stage authorizationGui = new Stage();
        authorizationGui.initModality(Modality.WINDOW_MODAL);
        authorizationGui.initOwner(clientGui);
        Scene authorizationScene = new Scene(authorizationParent);
        authorizationGui.setTitle("Authorization");
        authorizationGui.setScene(authorizationScene);
        AuthorizationController.stage = authorizationGui;

        authorizationGui.showAndWait();
        Controller.name = ((TextField) (authorizationScene.lookup("TextField"))).getText();
        Parent root = FXMLLoader.load(ClassLoader.getSystemClassLoader().getResource("server.fxml"));
        Scene scene = new Scene(root);
        clientGui.setTitle("Клиент чата");
        clientGui.setScene(scene);
        clientGui.setOnCloseRequest(event -> System.exit(0));
        clientGui.show();
    }
}
