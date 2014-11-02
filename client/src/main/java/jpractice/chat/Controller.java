package jpractice.chat;

/**
 * @author Alexander Vlasov
 */

import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import jpractice.chat.networks.*;

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
    private ObjectSender sender;
    private ObjectParser parser;
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
        network.send(editText.getText() + "\n");
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
        Node visual = person.getVisual();
        visual.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });
        personVBox.getChildren().addAll(person.getVisual());
    }


    private void setObjectHandler(ConcurrentHashMap<MyObjectInputStream, Object> queue) {
        objectHandler = new ObjectHandler(queue);
        objectHandler.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                Map.Entry<MyObjectInputStream, Object> entry = (Map.Entry) workerStateEvent.getSource().getValue();
                Object value = entry.getValue();
                MyObjectInputStream in = entry.getKey();
                if (value.getClass().equals(Person.class)) {
                    Person person = (Person) value;
                    if (person.isOnline()) {
                        personVBox.getChildren().addAll(person.getVisual());
                        network.getPersons().put(in, person);
                    } else {
                        personVBox.getChildren().removeAll(person.getVisual());
                        network.getPersons().remove(in);
                    }
                } else if (value.getClass().equals(String.class)) {
                    Person person = network.getPersons().get(in);
                    String text;
                    if (person == null) text = "Unknown: ";
                    else text = person.getName() + ": " + value;
                    commonArea.appendText(text + "\n");
                } else if (value.equals(Special.LostConnection)) {
                    System.out.println("Lost connection");
                    commonArea.clear();
                    personVBox.getChildren().clear();    // возможен первый элемент == Group не нужно удалять
                    commonArea.appendText("Потеряно соединение с сервером\n");
                    while (!connectToServer()) ;
                }
                objectHandler.restart();
            }
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
