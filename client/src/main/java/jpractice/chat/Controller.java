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

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Controller implements Initializable {
    final int serverPort = 20000;
    private List<Person> personList;
    private Person me = new Person("Alex");
    private ObjectSender sender;
    private ObjectParser parser;
    private ObjectHandler objectHandler;
    private Socket socket;
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
        sender.sendObject(me.getName() + ": " + editText.getText() + "\n");
        editText.clear();
    }

    private void setMe() {
        Random random = new Random();
        int b = random.nextInt();
        me = new Person("Client " + String.valueOf(b));
    }

    private void connectionEstablished() {
        connected = true;
        network.getParser().registerEmergency(String.class);
        network.getParser().registerEmergency(Special.class);
        network.getParser().registerEmergency(Person.class);
        BlockingQueue queue = new LinkedBlockingQueue();
        network.getParser().setEmergency(queue);
        setObjectHandler(queue);
        sender = network.getSender();
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

    private void setObjectHandler(BlockingQueue queue) {

        objectHandler = new ObjectHandler(queue);
        objectHandler.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                Object object = workerStateEvent.getSource().getValue();

                if (object.getClass().equals(String.class)) {
                    commonArea.appendText((String) object);
                }
                if (object.equals(Person.class)) {
                    Person person = (Person) object;
                    if (person.isOnline()) {
                        addPerson(person);
                    } else {
                        removePerson(person);
                    }
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
            setMe();
            socket = new Socket(InetAddress.getLocalHost(), serverPort);
            System.out.println(socket);
            network = new Network(socket);
            connectionEstablished();
            sender.sendObject(me);
            personList = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
