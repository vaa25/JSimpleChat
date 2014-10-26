package jpractice.chat.networks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Класс принимает объекты и выдает их по запросу
 *
 * @author Alexander Vlasov
 */
public class ObjectParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Set<Class> emergencyClasses;
    private ConcurrentHashMap<Class, BlockingQueue> map;
    private BlockingQueue emergency;

    public ObjectParser() {
        this.map = new ConcurrentHashMap<>();
        emergencyClasses = new HashSet<>();
    }

    public void registerEmergency(Class clazz) {
        emergencyClasses.add(clazz);
    }

    /**
     * Извлекает объект нужного класса
     *
     * @param clazz
     *
     * @return
     *
     * @throws InterruptedException
     */
    public Object take(Class clazz) throws InterruptedException {
        while (!map.containsKey(clazz)) {
            Thread.sleep(100);
        }
        Object object = getQueue(clazz).take();
        logger.info(Thread.currentThread().getName() + " ObjectParser успешно take " + object);
        return object;
    }

    private BlockingQueue getQueue(Class clazz) {
        BlockingQueue queue = new LinkedBlockingQueue();
        BlockingQueue oldQueue = map.putIfAbsent(clazz, queue);
        return oldQueue == null ? queue : oldQueue;
    }

    public void setEmergency(BlockingQueue emergency) {
        this.emergency = emergency;
    }

    /**
     * Ложит объект на хранение
     *
     * @param object
     */
    public synchronized void put(Object object) {
        Class clazz = object.getClass();
        getQueue(clazz).add(object);
        logger.info(Thread.currentThread().getName() + " ObjectParser успешно put " + object);
        if (emergencyClasses.contains(clazz)) {
            try {
                emergency.put(object);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
