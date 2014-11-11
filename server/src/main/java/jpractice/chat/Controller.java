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
import jpractice.chat.networks.MyObjectInputStream;
import jpractice.chat.networks.Network;
import jpractice.chat.networks.ObjectHandler;
import jpractice.chat.networks.Special;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Controller implements Initializable {
    final int serverPort = 20000;
    private ConcurrentHashMap<OutputStream, Person> oosPersonMap;
    private ObjectHandler objectHandler;
    private Network network;
    private List<Person> personList;
    private Buffer buffer;

    @FXML
    private TextArea commonArea;
    @FXML
    private VBox personVBox;
    @FXML
    private TextField editText;

    @FXML
    void textEntered(ActionEvent event) {
        String text = "Server: " + editText.getText();
        commonArea.appendText(text + "\n");
        sendToAll(text);
        buffer.add(text);
        editText.clear();
    }

    private void sendToAll(Object object) {
        Collection<Person> toRemove = network.sendToAll(object);
        for (Person person : toRemove) {
            System.out.println(person.getName() + " disconnected");
            personVBox.getChildren().removeAll(person.getVisual());
        }
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
                MyObjectInputStream in = entry.getKey();
                if (value.getClass().equals(Person.class)) {
                    Person person = (Person) value;
                    network.sendToAllExcept(in, person);
                    if (person.isOnline()) {
                        addPerson(in, person);
                        if (buffer.size() > 0) network.send(in, buffer.get());
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
                    buffer.add(text);
                } else if (value.equals(Special.LostConnection)) {
                    commonArea.appendText("Потеряно соединение с " + network.getPersons().get(in).getName() + "\n");
                    removePerson(in);

                } else System.out.println("Неизвестное значение: " + value);

            }
            objectHandler.restart();
        });
        objectHandler.start();
    }

    private void addPerson(MyObjectInputStream in, Person person) {
        network.getPersons().put(in, person);
        personVBox.getChildren().addAll(person.getVisual());
        personList.add(person);
        network.send(in, personList);
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
            ConcurrentHashMap<MyObjectInputStream, BlockingQueue> received = new ConcurrentHashMap<>();
            network = new Network(serverPort, received);
            setObjectHandler(received);
            personList = new ArrayList<>();
            buffer = new Buffer(10);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
