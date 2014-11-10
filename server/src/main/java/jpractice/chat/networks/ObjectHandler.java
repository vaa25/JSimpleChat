package jpractice.chat.networks;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Vlasov
 */
public class ObjectHandler extends Service<Map.Entry> {
    private ConcurrentHashMap<MyObjectInputStream, BlockingQueue> map;

    public ObjectHandler(ConcurrentHashMap<MyObjectInputStream, BlockingQueue> map) {
        this.map = map;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                while (true) {
                    Set<Map.Entry<MyObjectInputStream, BlockingQueue>> entries = map.entrySet();
                    Iterator<Map.Entry<MyObjectInputStream, BlockingQueue>> iterator = entries.iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<MyObjectInputStream, BlockingQueue> entry = iterator.next();
                        BlockingQueue queue = entry.getValue();
                        if (!queue.isEmpty()) {
                            System.out.println("Принял " + entry.getValue());
                            return entry;
                        }
                    }
                }
            }

        };
    }
}