package jpractice.chat;

/**
 * Сервер
 * @author Alexander Vlasov
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import jpractice.chat.networks.MyObjectInputStream;
import jpractice.chat.networks.NetworkServer;
import jpractice.chat.networks.ObjectHandler;
import jpractice.chat.networks.Special;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Controller implements Initializable {
    final int serverPort = 10181;
    private ConcurrentHashMap<OutputStream, Person> oosPersonMap;
    private ObjectHandler objectHandler;
    private NetworkServer networkServer;
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
        Collection<Person> toRemove = networkServer.sendToAll(object);
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
                    networkServer.sendToAllExcept(in, person);
                    if (person.isOnline()) {
                        addPerson(in, person);
                        if (buffer.size() > 0) networkServer.send(in, buffer.get());
                    } else {
                        removePerson(in);
                    }
                } else if (value.getClass().equals(String.class)) {
                    Person person = networkServer.getPersons().get(in);
                    String text;
                    if (person == null) text = "Unknown: ";
                    else text = person.getName() + ": " + value;
                    commonArea.appendText(text + "\n");
                    sendToAll(text);
                    buffer.add(text);
                } else if (value.equals(Special.LostConnection)) {
                    commonArea.appendText("Потеряно соединение с " + networkServer.getPersons().get(in).getName() + "\n");
                    removePerson(in);

                } else System.out.println("Неизвестное значение: " + value);

            }
            objectHandler.restart();
        });
        objectHandler.start();
    }

    private void addPerson(MyObjectInputStream in, Person person) {
        networkServer.getPersons().put(in, person);
        personVBox.getChildren().addAll(person.getVisual());
        personList.add(person);
        networkServer.send(in, personList);
    }

    private void removePerson(MyObjectInputStream in) {
        Person person = networkServer.getPersons().get(in);
        personVBox.getChildren().removeAll(person.getVisual());
        personList.remove(person);
        networkServer.remove(in);
        person.setOnline(false);
        networkServer.sendToAll(person);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ConcurrentHashMap<MyObjectInputStream, BlockingQueue> received = new ConcurrentHashMap<>();
            networkServer = new NetworkServer(serverPort, received);
            setObjectHandler(received);
            personList = new ArrayList<>();
            buffer = new Buffer(10);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
