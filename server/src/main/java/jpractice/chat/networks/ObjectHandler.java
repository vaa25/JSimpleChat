package jpractice.chat.networks;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Vlasov
 */
public class ObjectHandler extends Service<Map.Entry> {
    private ConcurrentHashMap map;

    public ObjectHandler(ConcurrentHashMap map) {
        this.map = map;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                while (map.isEmpty()) ;
                Set<Map.Entry> set = map.entrySet();
                Map.Entry res = set.iterator().next();
                map.remove(res.getKey());
                System.out.println("Принял " + res);
                return res;
            }
        };

    }
}
