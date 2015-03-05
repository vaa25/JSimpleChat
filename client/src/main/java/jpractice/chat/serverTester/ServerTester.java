package jpractice.chat.serverTester;

import jpractice.chat.websocket.Person;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Vlasov
 */
public class ServerTester {
    private List<WebSocketClient> clients;
    private volatile Boolean stop;
    private volatile int errors;

    public static void main(String[] args) {
        new ServerTester().doTest();
    }

    public void doTest() {
//        String destUri = "ws://localhost:8080/JSimpleChatServer/jsimplechat";
        String destUri = "ws://wizzardo.no-ip.org:10181/JSimpleChatServer/jsimplechat";
        Person me = new Person();
        clients = new ArrayList<>();
        stop = false;
        errors = 0;
        while (!stop) {
            try {
                WebSocketClient client = new WebSocketClient();
                client.setMaxIdleTimeout(15 * 60 * 1000);
                client.start();
                URI echoUri = new URI(destUri);
                ClientUpgradeRequest request = new ClientUpgradeRequest();
                request.setHeader("id", request.getKey());
                request.setHeader("name", URLEncoder.encode(me.getName(), "UTF-8"));
                me.setId(request.getKey());
                client.connect(new ClientWebSocketTester(this), echoUri, request).get();
                clients.add(client);
                System.out.println(clients.size() - errors);
            } catch (Exception e) {
                e.printStackTrace();
//                stop=true;
            }
        }


        for (WebSocketClient client : clients) {
            try {
                client.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public synchronized void stop() {
        stop = true;
    }

    public synchronized void addError(String reason) {
        errors++;
        System.out.println(reason + "\t" + errors);
    }
}
