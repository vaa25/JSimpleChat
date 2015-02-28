package jpractice.chat.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URLEncoder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Alexander Vlasov
 */
public class SimpleClient {
    private Session session;

    public SimpleClient(Person me, BlockingQueue<String> inbox) throws Exception {
//        String destUri = "ws://localhost:8080/JSimpleChatServer/jsimplechat";
        String destUri = "ws://wizzardo.no-ip.org:10181/JSimpleChatServer/jsimplechat";
        WebSocketClient client = new WebSocketClient();
        client.setMaxIdleTimeout(15 * 60 * 1000);
        client.start();
        URI echoUri = new URI(destUri);
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setHeader("id", request.getKey());
        request.setHeader("name", URLEncoder.encode(me.getName(), "UTF-8"));
        me.setId(request.getKey());
        Future<Session> sessionFuture = client.connect(new ClientWebSocket(inbox), echoUri, request);
        System.out.printf("Connecting to : %s%n", echoUri);
        session = sessionFuture.get();
    }

    public void send(Message message) {
        try {
            session.getRemote().sendStringByFuture(new Gson().toJson(message)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}