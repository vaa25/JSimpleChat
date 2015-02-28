package jpractice.chat;/**
 *
 * @author Alexander Vlasov
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
        Stage authorizationGui = new Stage();
        authorizationGui.initModality(Modality.WINDOW_MODAL);
        authorizationGui.initOwner(clientGui);
        Scene authorizationScene = new Scene(
                FXMLLoader.load(ClassLoader.getSystemClassLoader().getResource("authorization.fxml")));
        authorizationGui.setTitle("Authorization");
        authorizationGui.setScene(authorizationScene);
        AuthorizationController.stage = authorizationGui;
        authorizationGui.setOnCloseRequest((event -> System.exit(0)));
        authorizationGui.showAndWait();
        WSController.name = ((TextField) (authorizationScene.lookup("TextField"))).getText();
        clientGui.setTitle("Клиент чата");
        clientGui.setScene(new Scene(FXMLLoader.load(ClassLoader.getSystemClassLoader().getResource("client.fxml"))));
        clientGui.setOnCloseRequest(event -> System.exit(0));
        clientGui.show();
    }
}
