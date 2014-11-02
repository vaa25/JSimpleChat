package jpractice.chat;

/**
 * @author Alexander Vlasov
 */

import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import jpractice.chat.networks.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class Controller implements Initializable, NewPersonListener {
    final int serverPort = 20000;
    private ConcurrentHashMap<OutputStream, Person> oosPersonMap;
    private ObjectHandler objectHandler;
    private boolean connected;
    private Network network;

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
        objectHandler.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                Map.Entry<MyObjectInputStream, Object> entry = (Map.Entry) workerStateEvent.getSource().getValue();
                Object value = entry.getValue();
                MyObjectInputStream in = entry.getKey();
                if (value.getClass().equals(Person.class)) {
                    Person person = (Person) value;
                    network.getPersons().put(in, person);
                    network.sendToAll(person);
                    if (person.isOnline()) personVBox.getChildren().addAll(person.getVisual());
                    else personVBox.getChildren().removeAll(person.getVisual());
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
            }
        });
        objectHandler.start();
    }

    private void removePerson(MyObjectInputStream in) {
        personVBox.getChildren().removeAll(network.getPersons().get(in).getVisual());
        network.remove(in);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ConcurrentHashMap<MyObjectInputStream, Object> received = new ConcurrentHashMap<>();
            network = new Network(serverPort, received);
            setObjectHandler(received);
            connectionEstablished();

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
