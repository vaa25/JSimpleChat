package jpractice.chat.networks;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Alexander Vlasov
 */
public class ServerSocketHandler extends Service {
    private ServerSocket serverSocket;

    public ServerSocketHandler(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Socket call() throws Exception {
                return serverSocket.accept();
            }
        };
    }
}