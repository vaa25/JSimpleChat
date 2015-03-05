package jpractice.chat.serverTester;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * @author Alexander Vlasov
 */
@WebSocket()
public class ClientWebSocketTester {
    int errors = 0;
    private ServerTester serverTester;

    public ClientWebSocketTester(ServerTester serverTester) {
        this.serverTester = serverTester;
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        serverTester.addError(reason);
//        serverTester.stop();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
    }

    @OnWebSocketMessage
    public void onMessage(String json) {
    }

}
