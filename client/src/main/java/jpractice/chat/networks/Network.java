package jpractice.chat.networks;

import jpractice.chat.Person;
import jpractice.chat.networks.serializators.Serializator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Network {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private OutputStream out;
    private MyObjectInputStream in;
    private ConcurrentHashMap<MyObjectInputStream, Person> persons;
    private Socket socket;

    public Network(Socket socket, ConcurrentHashMap<MyObjectInputStream, BlockingQueue> received) throws IOException {
        this.socket = socket;
        out = socket.getOutputStream();
        System.out.println("1");
        in = new MyObjectInputStream(socket.getInputStream(), received);
        System.out.println("2");
        persons = new ConcurrentHashMap<>();
        new Thread(in).start();
    }

    public ConcurrentHashMap<MyObjectInputStream, Person> getPersons() {
        return persons;
    }

    public boolean send(Object object) {
        byte[] bytes = Serializator.debuild(object);
        System.out.println(Arrays.toString(bytes));
        System.out.println(Serializator.build(bytes));
        try {
            out.write(bytes);
            out.flush();
        } catch (IOException e) {
            System.out.println("Connection with server lost");
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
