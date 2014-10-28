package jpractice.chat.networks;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import jpractice.chat.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class Network {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ObjectParser parser;
    private int port;
    private ConcurrentHashMap<OutputStream, Person> map;
    private ConcurrentHashMap<OutputStream, MyObjectInputStream> map2;
    private ServerSocketHandler serverSocketHandler;
    private NewPersonListener personListener;

    public Network(ConcurrentHashMap<OutputStream, Person> map, int port, NewPersonListener controller) throws IOException {
        this.port = port;
        this.map = map;
        map2 = new ConcurrentHashMap<>();
        personListener = controller;
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
                    OutputStream oos = socket.getOutputStream();
                    System.out.println("2");
                    MyObjectInputStream ois = new MyObjectInputStream(socket.getInputStream());
                    new Thread(ois).start();
                    System.out.println("3");
                    map2.putIfAbsent(oos, ois);
//                    personListener.changePersonStatus(person);
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
//        byte[] bytes = Serializator.getBytes(object);
//        Enumeration<OutputStream> sockets = map.keys();
//        while (sockets.hasMoreElements()) {
//            OutputStream oos = sockets.nextElement();
//            try {
//                oos.write(bytes);
//                oos.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//                closed.add(map.remove(oos));
//                map2.remove(oos);
//            }
//        }
        return closed;
    }

    public ObjectParser getParser() {
        return parser;
    }

}
