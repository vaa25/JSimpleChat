package jpractice.chat.networks;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import jpractice.chat.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class Network {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ObjectParser parser;
    private int port;
    private ConcurrentHashMap<ObjectOutputStream, Person> map;
    private ServerSocketHandler serverSocketHandler;

    public Network(ConcurrentHashMap<ObjectOutputStream, Person> map, int port) throws IOException {
        this.port = port;
        this.map = map;
        launchServerSocketHandler();
        parser = new ObjectParser();

    }

    private void launchServerSocketHandler() throws IOException {

        serverSocketHandler = new ServerSocketHandler(new ServerSocket(port));
        serverSocketHandler.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                System.out.println("New connection found");
                try {
                    System.out.println("0");
                    Socket socket = (Socket) (workerStateEvent.getSource().getValue());
                    socket.setSoTimeout(1000 * 60 * 15);//15 минут
                    System.out.println("1");

                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    System.out.println("2");
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    System.out.println("3");
                    Person person = (Person) (ois.readObject());
                    map.putIfAbsent(oos, person);
                    new Thread(new ObjectReceiver(ois, person, parser)).start();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                serverSocketHandler.restart();
            }
        });
        serverSocketHandler.start();
    }

    public Collection<Person> sendToAll(Object object) {
        Collection<Person> closed = new ArrayList<>();
        Enumeration<ObjectOutputStream> sockets = map.keys();
        while (sockets.hasMoreElements()) {
            ObjectOutputStream oos = sockets.nextElement();
            try {
                    oos.writeObject(object);
                    oos.flush();
            } catch (IOException e) {
                    e.printStackTrace();
                closed.add(map.get(oos));
                map.remove(oos);
                }
        }
        return closed;
    }

    public ObjectParser getParser() {
        return parser;
    }

}
