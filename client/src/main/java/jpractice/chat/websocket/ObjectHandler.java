package jpractice.chat.websocket;

import com.google.gson.Gson;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.concurrent.BlockingQueue;

/**
 * @author Alexander Vlasov
 */
public class ObjectHandler extends Service<Message> {
    private BlockingQueue<String> inbox;

    public ObjectHandler(BlockingQueue<String> inbox) {
        this.inbox = inbox;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Message call() throws Exception {
                return new Gson().fromJson(inbox.take(), Message.class);
            }

        };
    }
}