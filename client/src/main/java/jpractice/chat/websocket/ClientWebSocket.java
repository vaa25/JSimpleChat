package jpractice.chat.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.concurrent.BlockingQueue;

/**
 * @author Alexander Vlasov
 */
@WebSocket()
public class ClientWebSocket {

    private Session session;
    private BlockingQueue<String> inbox;

    public ClientWebSocket(BlockingQueue<String> inbox) {
        this.inbox = inbox;
    }


    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
        inbox.add(new Gson().toJson("Вы отключены от сервера (" + reason + "). Для подключения вновь перезапустите клиент."));
        this.session = null;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.printf("Got connect: %s%n", session);
        this.session = session;
    }

    @OnWebSocketMessage
    public void onMessage(String json) {
        System.out.printf("Got msg: %s%n", json);
        inbox.add(json);
    }
}
