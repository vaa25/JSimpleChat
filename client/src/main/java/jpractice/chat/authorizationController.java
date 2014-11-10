package jpractice.chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AuthorizationController {
    @FXML
    public static Stage stage;
    @FXML
    private HBox actionParent;

    @FXML
    private Button okButton;

    @FXML
    private HBox okParent;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField textField;

    @FXML
    void nameEntered(ActionEvent event) {
        stage.close();
//        System.out.println(((Node)(event.getSource())).getParent().getParent().getParent());
    }


}
