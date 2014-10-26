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
import java.net.Socket;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Controller implements Initializable, NewPersonListener {
    final int serverPort = 20000;
    private ConcurrentHashMap<Socket, Person> personSocketMap;
    private ObjectParser parser;
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
        sendToAll("Server: " + editText.getText());
    }

    private void connectionEstablished() {
        connected = true;
        network.getParser().registerEmergency(String.class);
        network.getParser().registerEmergency(Special.class);
        network.getParser().registerEmergency(Person.class);
        BlockingQueue queue = new LinkedBlockingQueue();
        network.getParser().setEmergency(queue);
        setObjectHandler(queue);
    }

    private void sendToAll(Object object) {
        Collection<Person> toRemove = network.sendToAll(object);
        for (Person person : toRemove) {
            person.setOnline(false);

        }
        personVBox.getChildren().removeAll(toRemove);
    }

    private void setObjectHandler(BlockingQueue queue) {
        objectHandler = new ObjectHandler(queue);
        objectHandler.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                Object object = workerStateEvent.getSource().getValue();

                if (object.getClass().equals(String.class)) {
                    commonArea.appendText((String) object);
                    sendToAll(object);
                }

                if (object.equals(Special.NotReady)) {
                }
                objectHandler.restart();
            }
        });
        objectHandler.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            personSocketMap = new ConcurrentHashMap<>();
            network = new Network(personSocketMap, serverPort);
            connectionEstablished();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void addNewPerson(Person person) {
        if (person.isOnline()) {
            personVBox.getChildren().addAll(person.getVisual());
        }
    }
}
