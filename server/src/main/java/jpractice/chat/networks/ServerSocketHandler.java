package jpractice.chat.networks;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Alexander Vlasov
 */
public class ServerSocketHandler extends Service {
    private final int port;

    public ServerSocketHandler(int port) {
        this.port = port;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Socket call() throws Exception {
                return new ServerSocket(port).accept();
            }
        };
    }
}