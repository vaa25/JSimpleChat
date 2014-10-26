package jpractice.chat.networks;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import jpractice.chat.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class Network {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ObjectParser parser;
    private int port;
    private ConcurrentHashMap<Socket, Person> map;


    public Network(ConcurrentHashMap<Socket, Person> map, int port) throws IOException {
        this.port = port;
        this.map = map;
        setServerSocketHandler();
        parser = new ObjectParser();

    }

    private void setServerSocketHandler() {
        ServerSocketHandler serverSocketHandler = new ServerSocketHandler(port);
        serverSocketHandler.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                try {
                    Socket socket = (Socket) (workerStateEvent.getSource().getValue());
                    socket.setSoTimeout(1000 * 60 * 15);//15 минут
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    Person person = (Person) (ois.readObject());
                    map.putIfAbsent(socket, person);
                    new Thread(new ObjectReceiver(ois, parser)).start();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
        serverSocketHandler.start();
    }

    public Collection<Person> sendToAll(Object object) {
        Collection<Person> closed = new ArrayList<>();
        Enumeration<Socket> sockets = map.keys();
        while (sockets.hasMoreElements()) {
            Socket socket = sockets.nextElement();
            if (socket.isClosed()) {
                closed.add(map.get(socket));
                map.remove(socket);
            } else {
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(object);
                    oos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return closed;
    }

    public ObjectParser getParser() {
        return parser;
    }

}
