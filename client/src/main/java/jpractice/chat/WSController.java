package jpractice.chat;

/**
 * Клиент
 * @author Alexander Vlasov
 */

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jpractice.chat.websocket.*;
import org.eclipse.jetty.websocket.api.WebSocketException;

import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WSController implements Initializable {
    public static String name;
    private BlockingQueue<String> inbox;
    private SimpleClient network;
    private List<Person> personList;
    private Person me;
    private ObjectHandler objectHandler;
    @FXML
    private TextArea commonArea;
    @FXML
    private VBox personVBox;
    @FXML
    private TextField editText;


    @FXML
    void textEntered(ActionEvent event) {
        try {
            network.send(new Message(editText.getText()).setPerson(me));
        } catch (WebSocketException e) {
            if ("RemoteEndpoint unavailable, current state [CLOSED], expecting [OPEN or CONNECTED]".equals(e.getMessage())) {
                print("Сообщение те может быть отправлено, так как вы отключены от сервера");
            } else {
                print(e.getMessage());
            }
        }
        editText.clear();
    }

    private void setMe() {
        me = new Person(name);
    }

    private void print(String string) {
        inbox.add(new Gson().toJson(string));
    }

    private void removePerson(Person person) {
        ObservableList<Node> observableList = personVBox.getChildren();
        Node found = null;
        for (Node visual : observableList) {
            if (isSameVisual(visual, person)) {
                found = visual;
                break;
            }
        }
        observableList.remove(found);
        personList.remove(person);
    }

    private boolean isSameVisual(Node visual1, Person person) {
        return visual1.getUserData().equals(person.getId());
    }
    private void addPerson(Person person) {
        personList.add(person);
        Text visual = new Text(person.getName());
        visual.setUserData(person.getId());
        setOnMouseClicked(visual);
        fillRedVisualIfMe(visual);
        personVBox.getChildren().addAll(visual);
    }

    private void setOnMouseClicked(Text visual) {
        visual.setOnMouseClicked(event -> {
            editText.setText(((Text) event.getSource()).getText() + ", ");
            editText.requestFocus();
            editText.selectEnd();
        });
    }

    private void fillRedVisualIfMe(Text visual) {
        if (isSameVisual(visual, me)) {
            visual.setFill(Color.RED);
        }
    }

    private void refreshPersonList(List<Person> newPersonList) {
        personList.clear();
        ObservableList observableList = personVBox.getChildren();
        observableList.clear();
        newPersonList.forEach(this::addPerson);
    }

    private void setObjectHandler(BlockingQueue<String> inbox) {
        objectHandler = new ObjectHandler(inbox);
        objectHandler.setOnSucceeded(workerStateEvent -> {
            Message message = (Message) workerStateEvent.getSource().getValue();
            addOrRemovePersonIfNeed(message);
            refreshPersonListIfNeed(message);
            printTextIfNeed(message);
            doSpecialCommandsIfNeed(message);
            objectHandler.restart();
        });
        objectHandler.start();
    }

    private void doSpecialCommandsIfNeed(Message message) {
        if (message.getSpecial() != null) {
            if (message.getSpecial().equals(Special.LostConnection)) {
                System.out.println("Lost connection");
                personVBox.getChildren().clear();
                personList.clear();
                commonArea.appendText("Потеряно соединение с сервером\n");
                while (!connectToServer()) ;
            }
        }
    }

    private void printTextIfNeed(Message message) {
        if (message.getText() != null) {
            commonArea.appendText(message.getText() + "\n");
        }
    }

    private void refreshPersonListIfNeed(Message message) {
        if (message.getPersonList() != null) {
            refreshPersonList(message.getPersonList());
        }
    }

    private void addOrRemovePersonIfNeed(Message message) {
        if (message.getPerson() != null) {
            Person person = message.getPerson();
            if (person.isOnline()) {
                if (!personList.contains(person)) {
                    addPerson(person);
                }
            } else {
                removePerson(person);
            }
        }
    }

    private boolean connectToServer() {
        try {
            inbox = new LinkedBlockingQueue<>();
            network = new SimpleClient(me, inbox);
            commonArea.appendText("Соединение с сервером установлено\n");
            setObjectHandler(inbox);
            personList = new ArrayList<>();
        } catch (ConnectException e) {
            if ("Connection refused: connect".equals(e.getMessage())) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMe();
        if (!connectToServer()) {
            commonArea.appendText("Сервер не обнаружен");
            System.out.println("No server available");
            System.exit(0);
        }

    }

    class Authorize extends Application {

        @Override
        public void start(Stage stage) throws Exception {
            Parent root = FXMLLoader.load(ClassLoader.getSystemClassLoader().getResource("authorization.fxml"));

            Scene scene = new Scene(root);
            stage.setTitle("Authorization");
            stage.setScene(scene);

            stage.setOnCloseRequest(event -> System.exit(0));
            stage.show();
        }
    }
}
