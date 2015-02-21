package jpractice.chat.networks;

import jpractice.chat.networks.serializators.Serializator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private OutputStream out;
    private MyObjectInputStream in;

    public NetworkClient(Socket socket, ConcurrentHashMap<MyObjectInputStream, BlockingQueue> received) throws IOException {
        out = socket.getOutputStream();
        in = new MyObjectInputStream(socket.getInputStream(), received);
        new Thread(in).start();
    }

    public boolean send(Object object) {
        byte[] bytes = Serializator.debuild(object);
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
