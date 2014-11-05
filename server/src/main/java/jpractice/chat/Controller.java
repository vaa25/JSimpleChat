package jpractice.chat;

/**
 * @author Alexander Vlasov
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import jpractice.chat.networks.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Controller implements Initializable, NewPersonListener {
    final int serverPort = 20000;
    private ConcurrentHashMap<OutputStream, Person> oosPersonMap;
    private ObjectHandler objectHandler;
    private boolean connected;
    private Network network;
    private List<Person> personList;

    @FXML
    private TextArea commonArea;
    @FXML
    private VBox personVBox;
    @FXML
    private TextField editText;

    @FXML
    void textEntered(ActionEvent event) {
        String text = "Server: " + editText.getText() + "\n";
        commonArea.appendText(text);
        sendToAll(text);
        editText.clear();
    }

    private void connectionEstablished() {
        connected = true;

    }

    private void sendToAll(Object object) {
        Collection<Person> toRemove = network.sendToAll(object);
        for (Person person : toRemove) {
            System.out.println(person.getName() + " disconnected");
            personVBox.getChildren().removeAll(person.getVisual());
        }
    }

    private void setObjectHandler(ConcurrentHashMap<MyObjectInputStream, Object> queue) {
        objectHandler = new ObjectHandler(queue);
        objectHandler.setOnSucceeded(workerStateEvent -> {
            Map.Entry<MyObjectInputStream, Object> entry = (Map.Entry) workerStateEvent.getSource().getValue();
            Object value = entry.getValue();
            MyObjectInputStream in = entry.getKey();
            if (value.getClass().equals(Person.class)) {
                Person person = (Person) value;
                network.sendToAll(person);
                if (person.isOnline()) {
                    addPerson(in, person);
                    network.send(in, personList);
                } else {
                    removePerson(in);
                }
            } else if (value.getClass().equals(String.class)) {
                Person person = network.getPersons().get(in);
                String text;
                if (person == null) text = "Unknown: ";
                else text = person.getName() + ": " + value;
                commonArea.appendText(text + "\n");
                sendToAll(text);
            } else if (value.equals(Special.LostConnection)) {
                commonArea.appendText("Потеряно соединение с " + network.getPersons().get(in).getName() + "\n");
                removePerson(in);

            } else System.out.println("Неизвестное значение: " + value);
            objectHandler.restart();
        });
        objectHandler.start();
    }

    private void addPerson(MyObjectInputStream in, Person person) {
        network.getPersons().put(in, person);
        personVBox.getChildren().addAll(person.getVisual());
        network.send(in, personList);
        personList.add(person);
    }

    private void removePerson(MyObjectInputStream in) {
        Person person = network.getPersons().get(in);
        personVBox.getChildren().removeAll(person.getVisual());
        personList.remove(person);
        network.remove(in);
        person.setOnline(false);
        network.sendToAll(person);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ConcurrentHashMap<MyObjectInputStream, Object> received = new ConcurrentHashMap<>();
            network = new Network(serverPort, received);
            setObjectHandler(received);
            connectionEstablished();
            personList = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void changePersonStatus(Person person) {
        if (person.isOnline()) {
            System.out.println(person.getName() + " connected");
            personVBox.getChildren().addAll(person.getVisual());
        } else {
            System.out.println(person.getName() + " disconnected");
            personVBox.getChildren().removeAll(person.getVisual());
        }
    }
}
