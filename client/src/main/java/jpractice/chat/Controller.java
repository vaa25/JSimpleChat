package jpractice.chat;

/**
 * @author Alexander Vlasov
 */

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import jpractice.chat.networks.MyObjectInputStream;
import jpractice.chat.networks.Network;
import jpractice.chat.networks.ObjectHandler;
import jpractice.chat.networks.Special;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Controller implements Initializable {
    final int serverPort = 20000;
    private List<Person> personList;
    private Person me;
    private ObjectHandler objectHandler;
    private Socket socket;
    private boolean connected;
    private Network network;
    private ConcurrentHashMap<MyObjectInputStream, Object> received;
    @FXML
    private TextArea commonArea;
    @FXML
    private VBox personVBox;
    @FXML
    private TextField editText;

    @FXML
    void textEntered(ActionEvent event) {
        network.send(editText.getText());
        editText.clear();
    }

    private void setMe() {
        Random random = new Random();
        int b = random.nextInt();
        me = new Person("Client " + String.valueOf(b));
    }

    private void connectionEstablished() {
        connected = true;
        System.out.println(" Connection established");
    }

    private void removePerson(Person person) {
        personList.remove(person);
        personVBox.getChildren().remove(person.getVisual());
    }

    private void addPerson(Person person) {
        personList.add(person);
        Text visual = (Text) person.getVisual();
        visual.setOnMouseClicked(event -> {
            editText.setText(((Text) event.getSource()).getText() + ", ");
            editText.requestFocus();
            editText.selectEnd();
        });
        if (me.getVisual().equals(visual)) visual.setFill(Color.RED);
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

    private void setObjectHandler(ConcurrentHashMap<MyObjectInputStream, Object> queue) {
        objectHandler = new ObjectHandler(queue);
        objectHandler.setOnSucceeded(workerStateEvent -> {
            Map.Entry<MyObjectInputStream, Object> entry = (Map.Entry) workerStateEvent.getSource().getValue();
            Object value = entry.getValue();
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
            objectHandler.restart();
        });
        objectHandler.start();
    }

    private boolean connectToServer() {
        try {
            received = new ConcurrentHashMap<>();
            socket = new Socket(InetAddress.getLocalHost(), serverPort);
            commonArea.appendText("Соединение с сервером установлено\n");
            System.out.println(socket);
            network = new Network(socket, received);
            setObjectHandler(received);
            connectionEstablished();
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
}
