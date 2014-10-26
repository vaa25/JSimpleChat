package jpractice.chat;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Vlasov
 */
public class Server {
    private final int port = 20000;
    private ConcurrentHashMap<Person, Socket> personMap = new ConcurrentHashMap<>();

    public Server() {

    }

    public static void main(String[] args) {
        Server server = new Server();
    }
}
