package jpractice.chat.networks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;

/**
 * Через этот класс можно посылать объекты через заданный ObjectOutputStream
 *
 * @author Alexander Vlasov
 */
public class ObjectSender {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Set<ObjectOutputStream> objectOutputStreamSet;

    public ObjectSender(Set<ObjectOutputStream> objectOutputStreamSet) {
        this.objectOutputStreamSet = objectOutputStreamSet;
    }

    public void sendObjectToAll(Object object) {
        logger.info(Thread.currentThread().getName() + " ObjectSender пытается послать сообщение " + object, object);
        for (ObjectOutputStream objectOutputStream : objectOutputStreamSet)
            try {
                objectOutputStream.writeObject(object);
                objectOutputStream.flush();
            } catch (IOException e) {
                logger.error(Thread.currentThread().getName() + "ObjectReceiver (" + Thread.currentThread().getName() + ") IOException: ", e);
            }
        logger.info(Thread.currentThread().getName() + " ObjectSender послал сообщение " + object, object);
    }
}
