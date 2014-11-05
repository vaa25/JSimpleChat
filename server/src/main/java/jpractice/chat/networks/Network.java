package jpractice.chat.networks;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import jpractice.chat.Person;
import jpractice.chat.networks.serializators.Serializator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class Network {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private int port;
    private ConcurrentHashMap<MyObjectInputStream, Person> persons;
    private ConcurrentHashMap<MyObjectInputStream, OutputStream> map2;
    private ConcurrentHashMap<MyObjectInputStream, Object> received;
    private ServerSocketHandler serverSocketHandler;

    public Network(int port, ConcurrentHashMap received) throws IOException {
        this.port = port;
        map2 = new ConcurrentHashMap<>();
        launchServerSocketHandler();
        this.received = received;
        persons = new ConcurrentHashMap<>();

    }

    public void remove(MyObjectInputStream in) {
        persons.remove(in);
        map2.remove(in);
        received.remove(in);
    }

    public ConcurrentHashMap<MyObjectInputStream, Person> getPersons() {
        return persons;
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
                    MyObjectInputStream ois = new MyObjectInputStream(socket.getInputStream(), received);
                    new Thread(ois).start();
                    System.out.println("3");
                    map2.putIfAbsent(ois, oos);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                serverSocketHandler.restart();
            }
        });
        serverSocketHandler.start();
    }

    public void send(MyObjectInputStream in, Object object) {
        try {
            OutputStream out = map2.get(in);
            out.write(Serializator.debuild(object));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Collection<Person> sendToAll(Object object) {
        Collection<Person> closed = new ArrayList<>();
        byte[] bytes = Serializator.debuild(object);
        System.out.println(Arrays.toString(bytes));
        System.out.println(Serializator.build(bytes));
        Collection<OutputStream> sockets = map2.values();
        System.out.println(sockets.toString());
        for (OutputStream oos : sockets) {
            try {
                oos.write(bytes);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
//                Person person=map.remove(oos);
//                person.setOnline(false);
//                closed.add(person);
//                MyObjectInputStream in=map2.get(oos);
//                persons.remove(in);
//                received.remove(in);
//                map2.remove(oos);
            }
        }
        return closed;
    }


}
