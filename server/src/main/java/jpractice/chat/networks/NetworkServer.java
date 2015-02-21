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
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class NetworkServer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final int MAX_AMOUNT_OF_CLIENTS = 10;
    private int port;
    private ConcurrentHashMap<MyObjectInputStream, Person> persons;
    private ConcurrentHashMap<MyObjectInputStream, OutputStream> outStreams;
    private ConcurrentHashMap<MyObjectInputStream, BlockingQueue> queues;
    private ServerSocketHandler serverSocketHandler;

    public NetworkServer(int port, ConcurrentHashMap queues) throws IOException {
        this.port = port;
        outStreams = new ConcurrentHashMap<>();
        launchServerSocketHandler();
        this.queues = queues;
        persons = new ConcurrentHashMap<>();

    }

    public void remove(MyObjectInputStream in) {
        persons.remove(in);
        outStreams.remove(in);
        queues.remove(in);
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
                    Socket socket = (Socket) (workerStateEvent.getSource().getValue());
                    socket.setSoTimeout(1000 * 60 * 15);//15 минут
                    OutputStream oos = socket.getOutputStream();
                    if (outStreams.size() <= MAX_AMOUNT_OF_CLIENTS) {
                        MyObjectInputStream ois = new MyObjectInputStream(socket.getInputStream(), queues);
                        new Thread(ois).start();
                        outStreams.putIfAbsent(ois, oos);
                    } else {
                        oos.write(Serializator.debuild("Сервер переполнен. Попробуйте подключиться позже."));
                        oos.flush();
                        oos.close();
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                serverSocketHandler.restart();
            }
        });
        serverSocketHandler.start();
    }

    public synchronized Collection<Person> sendToAllExcept(MyObjectInputStream in, Object object) {
        Collection<Person> closed = new ArrayList<>();
        byte[] bytes = Serializator.debuild(object);
        Collection<OutputStream> sockets = outStreams.values();
        OutputStream except = outStreams.get(in);
        for (OutputStream oos : sockets) {
            if (oos != except) {
                try {
                    oos.write(bytes);
                    oos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Отправил " + object + " ко всем кроме одного");
        return closed;
    }

    public synchronized void send(MyObjectInputStream in, Object object) {
        try {
            OutputStream out = outStreams.get(in);
            out.write(Serializator.debuild(object));
            out.flush();
            System.out.println("Отправил " + object + " к " + persons.get(in).getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized Collection<Person> sendToAll(Object object) {
        Collection<Person> closed = new ArrayList<>();
        byte[] bytes = Serializator.debuild(object);
        Collection<OutputStream> sockets = outStreams.values();
        for (OutputStream oos : sockets) {
            try {
                oos.write(bytes);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Отправил " + object + " ко всем ");
        return closed;
    }


}
