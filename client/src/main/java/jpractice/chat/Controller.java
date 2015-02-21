package jpractice.chat;

/**
 * Клиент
 * @author Alexander Vlasov
 */

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jpractice.chat.networks.MyObjectInputStream;
import jpractice.chat.networks.NetworkClient;
import jpractice.chat.networks.ObjectHandler;
import jpractice.chat.networks.Special;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Controller implements Initializable {
    public static String name;
    final int serverPort = 10181;
    private List<Person> personList;
    private Person me;
    private ObjectHandler objectHandler;
    private Socket socket;
    private NetworkClient network;
    private ConcurrentHashMap<MyObjectInputStream, BlockingQueue> received;
    @FXML
    private TextArea commonArea;
    @FXML
    private VBox personVBox;
    @FXML
    private TextField editText;
    @FXML
    private Label nameLabel;

    @FXML
    void textEntered(ActionEvent event) {
        network.send(editText.getText());
        editText.clear();
    }

    private void setMe() {
        nameLabel.setText(name);
        me = new Person(name);

    }

    private void removePerson(Person person) {
        removeVisual(person.getVisual());
        personList.remove(person);
    }

    private void removeVisual(Node visual) {
        ObservableList<Node> observableList = personVBox.getChildren();
        for (Node o : observableList) {
            if (isSameVisual(o, visual)) {
                visual = o;
                break;
            }
        }
        observableList.remove(visual);
    }

    private boolean isSameVisual(Node visual1, Node visual2) {
        return ((Text) visual1).getText().equals(((Text) visual2).getText());
    }

    private void addPerson(Person person) {
        personList.add(person);
        Text visual = (Text) person.getVisual();
        visual.setOnMouseClicked(event -> {
            editText.setText(((Text) event.getSource()).getText() + ", ");
            editText.requestFocus();
            editText.selectEnd();
        });
        if (me.getVisual().toString().equals(visual.toString())) {
            visual.setFill(Color.RED);
        }
        personVBox.getChildren().addAll(person.getVisual());
    }

    private void refreshPersonList(List<Person> newPersonList) {
        personList.retainAll(newPersonList);
        newPersonList.removeAll(personList);
        ObservableList observableList = personVBox.getChildren();
        for (int i = 0; i < observableList.size(); i++) {
            if (personList.contains(observableList.get(i))) continue;
            else {
                observableList.remove(i--);
            }
        }
        newPersonList.forEach(this::addPerson);
    }

    private void setObjectHandler(ConcurrentHashMap<MyObjectInputStream, BlockingQueue> map) {
        objectHandler = new ObjectHandler(map);
        objectHandler.setOnSucceeded(workerStateEvent -> {
            Map.Entry<MyObjectInputStream, BlockingQueue> entry = (Map.Entry) workerStateEvent.getSource().getValue();
            BlockingQueue queue = entry.getValue();

            while (!queue.isEmpty()) {
                Object value = null;
                try {
                    value = queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (value.getClass().equals(Person.class)) {
                    Person person = (Person) value;
                    if (person.isOnline()) {
                        addPerson(person);
                    } else {
                        removePerson(person);
                    }
                } else if (value.getClass().equals(ArrayList.class)) {
                    refreshPersonList((List) value);

                } else if (value.getClass().equals(String.class)) {
                    commonArea.appendText(value + "\n");
                } else if (value.equals(Special.LostConnection)) {
                    System.out.println("Lost connection");
                    personVBox.getChildren().clear();    // возможен первый элемент == Group не нужно удалять
                    commonArea.appendText("Потеряно соединение с сервером\n");
                    while (!connectToServer()) ;
                }
            }
            objectHandler.restart();
        });
        objectHandler.start();
    }

    private boolean connectToServer() {
        try {
            received = new ConcurrentHashMap<>();
            socket = new Socket(InetAddress.getLocalHost(), serverPort);
            commonArea.appendText("Соединение с сервером установлено\n");
            network = new NetworkClient(socket, received);
            setObjectHandler(received);
            personList = new ArrayList<>();
            network.send(me);
        } catch (ConnectException e) {
            if ("Connection refused: connect".equals(e.getMessage())) {
                return false;
            }
        } catch (IOException e) {
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
